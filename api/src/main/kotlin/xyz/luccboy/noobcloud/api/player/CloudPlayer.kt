package xyz.luccboy.noobcloud.api.player

import xyz.luccboy.noobcloud.api.server.Server
import java.util.Optional
import java.util.UUID

/**
 * Represents a player
 */
@Suppress("INAPPLICABLE_JVM_NAME")
interface CloudPlayer {
    /**
     * The username of the player
     */
    @get:JvmName("username")
    val username: String

    /**
     * The uuid of the player
     */
    @get:JvmName("uuid")
    val uuid: UUID

    /**
     * The server the player is currently connected to
     * @return An Optional of the [server][Server]
     */
    @JvmName("getServer")
    fun getServer(): Optional<Server>

    /**
     * Connects the player to a server
     * @param serverName The target server
     */
    @JvmName("connect")
    fun connect(serverName: String)
}