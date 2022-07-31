package xyz.luccboy.noobcloud.api

import xyz.luccboy.noobcloud.api.group.Group
import xyz.luccboy.noobcloud.api.player.CloudPlayer
import xyz.luccboy.noobcloud.api.server.Server
import java.util.Optional
import java.util.UUID

/**
 * The API of NoobCloud
 */
@Suppress("INAPPLICABLE_JVM_NAME")
abstract class NoobCloudAPI {

    companion object {
        @JvmStatic lateinit var instance: NoobCloudAPI
            private set
    }

    init {
        instance = this
    }

    /**
     * Gets all connected players
     * @return A list of all [players][CloudPlayer]
     */
    @JvmName("getAllPlayers")
    abstract fun getAllPlayers(): List<CloudPlayer>

    /**
     * Gets a player by his uuid
     * @param uuid The uuid
     * @return An Optional of the [player][CloudPlayer]
     */
    @JvmName("getCloudPlayer")
    abstract fun getCloudPlayer(uuid: UUID): Optional<CloudPlayer>

    /**
     * Gets a player by his name
     * @param username The username
     * @return An Optional of the [player][CloudPlayer]
     */
    @JvmName("getCloudPlayer")
    abstract fun getCloudPlayer(username: String): Optional<CloudPlayer>

    /**
     * Sends a player to a server
     * @param cloudPlayer The player
     * @param serverName The target server
     */
    @JvmName("connectCloudPlayer")
    abstract fun connectCloudPlayer(cloudPlayer: CloudPlayer, serverName: String)

    /**
     * Gets the username of a player by his uuid
     * @param uuid The uuid
     * @return An Optional of the username
     */
    @JvmName("getUsernameByUUID")
    abstract fun getUsernameByUUID(uuid: UUID): Optional<String>

    /**
     * Gets the uuid of a player by his name
     * @param username The username
     * @return An Optional of the uuid
     */
    @JvmName("getUUIDByUsername")
    abstract fun getUUIDByUsername(username: String): Optional<UUID>

    /**
     * Gets all registered groups
     * @return A list containing all [groups][Group]
     */
    @JvmName("getAllGroups")
    abstract fun getAllGroups(): List<Group>

    /**
     * Gets all registered groups with the type [GroupType.PROXY]
     * @return A list containing all proxy groups
     */
    @JvmName("getAllProxyGroups")
    abstract fun getAllProxyGroups(): List<Group>

    /**
     * Gets all registered groups with the type [GroupType.GAME]
     * @return A list containing all game groups
     */
    @JvmName("getAllGameGroups")
    abstract fun getAllGameGroups(): List<Group>

    /**
     * Gets a proxy group by its name
     * @param name The name of the group
     * @return An Optional of the [Group]
     */
    @JvmName("getGroup")
    abstract fun getGroup(name: String): Optional<Group>

    /**
     * Gets the online-count of a group
     * @param groupName The name of the group
     * @return An integer representing the online-count
     */
    @JvmName("getGroupOnlineCount")
    abstract fun getGroupOnlineCount(groupName: String): Int

    /**
     * Gets all servers
     * @return A list containing all [servers][Server]
     */
    @JvmName("getAllServers")
    abstract fun getAllServers(): List<Server>

    /**
     * Gets all proxy servers
     * @return A list containing all [proxy servers][Server]
     */
    @JvmName("getAllProxyServers")
    abstract fun getAllProxyServers(): List<Server>

    /**
     * Gets all game servers
     * @return A list containing all [game servers][Server]
     */
    @JvmName("getAllGameServers")
    abstract fun getAllGameServers(): List<Server>

    /**
     * Gets a server by its uuid
     * @param uuid The uuid of the server
     * @return An Optional of the [Server]
     */
    @JvmName("getServerByUUID")
    abstract fun getServerByUUID(uuid: UUID): Optional<Server>

    /**
     * Gets a server by its name
     * @param name The name of the server
     * @return An Optional of the [Server]
     */
    @JvmName("getServerByName")
    abstract fun getServerByName(name: String): Optional<Server>

    /**
     * Starts a server
     * @param groupName The name of the group
     */
    @JvmName("startServer")
    abstract fun startServer(groupName: String)

    /**
     * Stops a server
     * @param name The name of the server
     */
    @JvmName("stopServer")
    abstract fun stopServer(name: String)

    /**
     * Gets the motd of a server
     * @param serverName The target server
     * @return The motd of the server
     */
    abstract fun getMotd(serverName: String): String


    /**
     * Sets the motd of a server
     * @param serverName The target server
     * @param motd The new motd
     */
    abstract fun setMotd(serverName: String, motd: String)
}