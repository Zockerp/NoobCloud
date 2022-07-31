package xyz.luccboy.noobcloud.plugin.shared.database

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.velocitypowered.api.proxy.Player
import io.github.heliumdioxid.database.api.data.ConnectionData
import io.github.heliumdioxid.database.mysql.MySQLConnectionHandler
import io.github.heliumdioxid.database.mysql.MySQLConnectionHandler.UpdateResult
import io.github.heliumdioxid.database.mysql.MySQLDatabaseConnection
import io.github.heliumdioxid.database.mysql.config.MySQLConnectionConfig
import io.github.heliumdioxid.database.mysql.utils.Function
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture

class DatabaseManager(private val enabled: Boolean, private val host: String, private val user: String, private val password: String, private val database: String, private val port: Int) {

    private lateinit var databaseConnection: MySQLDatabaseConnection
    private lateinit var connectionHandler: MySQLConnectionHandler

    fun connect(): DatabaseManager {
        if (enabled) {
            val connectionData = ConnectionData(host, user, password, database, port)
            val connectionConfig = MySQLConnectionConfig(connectionData)
            connectionConfig.applyDefaultHikariConfig()

            databaseConnection = MySQLDatabaseConnection(connectionConfig)
            connectionHandler = this.databaseConnection.connect().join().orElseThrow { SQLException("Database-connection could not be established!") }

            executeUpdate("CREATE TABLE IF NOT EXISTS playerdata (uuid VARCHAR(36), name TEXT, PRIMARY KEY(uuid))").join()
        }
        return this
    }

    fun disconnect() {
        if (enabled) {
            databaseConnection.disconnect().join()
        }
    }

    private fun executeUpdate(query: String?, vararg parameters: String?): CompletableFuture<UpdateResult> {
        return CompletableFuture.supplyAsync { connectionHandler.executeUpdate(query, *parameters) }
    }

    private fun <T> executeQuery(consumer: Function<ResultSet, T>, defaultValue: T, query: String?, vararg parameters: String?): CompletableFuture<T> {
        return CompletableFuture.supplyAsync { connectionHandler.executeQuery(consumer, defaultValue, query, *parameters) }
    }

    // Player-Database
    private val playerData: BiMap<String, UUID> = HashBiMap.create()

    fun insertPlayer(player: Player) {
        playerData[player.username] = player.uniqueId
        if (enabled) {
            executeUpdate("INSERT INTO playerdata (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name=?", player.uniqueId.toString(), player.username, player.username).join()
        }
    }

    fun getUUIDByName(name: String): Optional<UUID> {
        return if (enabled) {
            if (playerData.containsKey(name)) {
                Optional.of(playerData[name]!!)
            } else {
                executeQuery({ resultSet ->
                    if (resultSet.next()) {
                        val uuid: UUID = UUID.fromString(resultSet.getString("uuid"))
                        playerData[name] = uuid
                        return@executeQuery Optional.of(uuid)
                    }
                    return@executeQuery Optional.empty()
                }, Optional.empty(), "SELECT uuid FROM playerdata WHERE name=?", name).join()
            }
        } else {
            Optional.empty()
        }
    }

    fun getNameByUUID(uuid: UUID): Optional<String> {
        return if (enabled) {
            if (playerData.containsValue(uuid)) {
                Optional.of(playerData.inverse()[uuid]!!)
            } else {
                executeQuery({ resultSet ->
                    if (resultSet.next()) {
                        val name: String = resultSet.getString("name")
                        playerData[name] = uuid
                        return@executeQuery Optional.of(name)
                    }
                    return@executeQuery Optional.empty()
                }, Optional.empty(), "SELECT name FROM playerdata WHERE uuid=?", uuid.toString()).join()
            }
        } else {
            Optional.empty()
        }
    }

}