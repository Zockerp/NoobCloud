package xyz.luccboy.noobcloud.plugin.shared.database

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoException
import com.velocitypowered.api.proxy.Player
import io.github.heliumdioxid.database.api.data.ConnectionData
import io.github.heliumdioxid.database.mongo.MongoConnectionHandler
import io.github.heliumdioxid.database.mongo.MongoDatabaseConnection
import io.github.heliumdioxid.database.mongo.config.MongoConnectionConfig
import io.github.heliumdioxid.database.mysql.MySQLConnectionHandler
import io.github.heliumdioxid.database.mysql.MySQLConnectionHandler.UpdateResult
import io.github.heliumdioxid.database.mysql.MySQLDatabaseConnection
import io.github.heliumdioxid.database.mysql.config.MySQLConnectionConfig
import io.github.heliumdioxid.database.mysql.utils.Function
import org.bson.Document
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture

class DatabaseManager(private val type: DatabaseType, private val host: String, private val user: String, private val password: String, private val database: String, private val port: Int) {

    // MySQL
    private lateinit var mysqlDatabaseConnection: MySQLDatabaseConnection
    private lateinit var mysqlConnectionHandler: MySQLConnectionHandler
    // MongoDB
    private lateinit var mongoDatabaseConnection: MongoDatabaseConnection
    private lateinit var mongoConnectionHandler: MongoConnectionHandler

    fun connect(): DatabaseManager {
        if (type == DatabaseType.MYSQL) {
            val connectionData = ConnectionData(host, user, password, database, port)
            val connectionConfig = MySQLConnectionConfig(connectionData)
            connectionConfig.applyDefaultHikariConfig()

            mysqlDatabaseConnection = MySQLDatabaseConnection(connectionConfig)
            mysqlConnectionHandler = mysqlDatabaseConnection.connect().join().orElseThrow { SQLException("Database-connection could not be established!") }

            executeUpdate("CREATE TABLE IF NOT EXISTS playerdata (uuid VARCHAR(36), name TEXT, PRIMARY KEY(uuid))").join()
        } else if (type == DatabaseType.MONGODB) {
            val connectionData = ConnectionData(host, user, password, database, port)
            val connectionConfig = MongoConnectionConfig(connectionData)
            connectionConfig.mongoClientSettings = MongoClientSettings.builder().applyConnectionString(
                ConnectionString("mongodb://" + connectionData.username + ":" + connectionData.password + "@" + connectionData.host + ":" + connectionData.port + "/" + connectionData.database)
            ).build()

            mongoDatabaseConnection = MongoDatabaseConnection(connectionConfig)
            mongoConnectionHandler = mongoDatabaseConnection.connect().join().orElseThrow { MongoException("Database-connection could not be established!") }

            // Create playerdata collection when connection established
            CompletableFuture.supplyAsync { mongoConnectionHandler.mongoDatabase.createCollection("playerdata") }
        }
        return this
    }

    fun disconnect() {
        if (type == DatabaseType.MYSQL) {
            mysqlDatabaseConnection.disconnect().join()
        } else if (type == DatabaseType.MONGODB) {
            mongoDatabaseConnection.disconnect().join()
        }
    }

    private fun executeUpdate(query: String?, vararg parameters: String?): CompletableFuture<UpdateResult> {
        return CompletableFuture.supplyAsync { mysqlConnectionHandler.executeUpdate(query, *parameters) }
    }

    private fun <T> executeQuery(consumer: Function<ResultSet, T>, defaultValue: T, query: String?, vararg parameters: String?): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { mysqlConnectionHandler.executeQuery(consumer, defaultValue, query, *parameters) }
    }

    private fun insertDocument(uuid: UUID, name: String) {
        mongoConnectionHandler.getDocument("playerdata", "uuid", uuid.toString()).join().ifPresentOrElse({
            mongoConnectionHandler.updateDocument("playerdata", "uuid", uuid.toString(), Document(mapOf("uuid" to uuid.toString(), "name" to name)))
        }, {
            mongoConnectionHandler.insertDocument("playerdata", Document(mapOf("uuid" to uuid.toString(), "name" to name)))
        })
    }

    // Player-Database
    private val playerData: BiMap<UUID, String> = HashBiMap.create()

    fun insertPlayer(player: Player) {
        playerData[player.uniqueId] = player.username
        when (type) {
            DatabaseType.MYSQL -> executeUpdate("INSERT INTO playerdata (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=?", player.uniqueId.toString(), player.username, player.username).join()
            DatabaseType.MONGODB -> insertDocument(player.uniqueId, player.username)
            else -> {}
        }
    }

    fun getUUIDByName(name: String): Optional<UUID> {
        if (playerData.containsValue(name)) return Optional.of(playerData.inverse()[name]!!)
        return when (type) {
            DatabaseType.MYSQL -> {
                executeQuery({ resultSet ->
                    if (resultSet.next()) {
                        val uuid: UUID = UUID.fromString(resultSet.getString("uuid"))
                        playerData[uuid] = name
                        return@executeQuery Optional.of(uuid)
                    }
                    return@executeQuery Optional.empty()
                }, Optional.empty(), "SELECT uuid FROM playerdata WHERE name=?", name).join()
            }
            DatabaseType.MONGODB -> {
                val document: Optional<Document> = mongoConnectionHandler.getDocument("playerdata", "name", name).join()
                if (document.isPresent) {
                    val uuid: UUID = UUID.fromString(document.get().getString("uuid"))
                    playerData[uuid] = name
                    return Optional.of(uuid)
                } else {
                    return Optional.empty()
                }
            }
            else -> Optional.empty()
        }
    }

    fun getNameByUUID(uuid: UUID): Optional<String> {
        if (playerData.containsKey(uuid)) return Optional.of(playerData[uuid]!!)
        return when (type) {
            DatabaseType.MYSQL -> {
                executeQuery({ resultSet ->
                    if (resultSet.next()) {
                        val name: String = resultSet.getString("name")
                        playerData[uuid] = name
                        return@executeQuery Optional.of(name)
                    }
                    return@executeQuery Optional.empty()
                }, Optional.empty(), "SELECT name FROM playerdata WHERE uuid=?", uuid.toString()).join()
            }
            DatabaseType.MONGODB -> {
                val document: Optional<Document> = mongoConnectionHandler.getDocument("playerdata", "uuid", uuid.toString()).join()
                if (document.isPresent) {
                    val name: String = document.get().getString("name")
                    playerData[uuid] = name
                    return Optional.of(name)
                } else {
                    return Optional.empty()
                }
            }
            else -> Optional.empty()
        }
    }

}

enum class DatabaseType {
    MYSQL,
    MONGODB,
    DISABLED
}