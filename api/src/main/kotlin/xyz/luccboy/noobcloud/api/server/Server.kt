package xyz.luccboy.noobcloud.api.server

import xyz.luccboy.noobcloud.api.group.GroupType
import java.util.UUID

/**
 * Represents a server
 */
@Suppress("INAPPLICABLE_JVM_NAME")
interface Server {
    /**
     * The name of the server
     */
    @get:JvmName("getName")
    val name: String

    /**
     * The uuid of the server
     */
    @get:JvmName("getUUID")
    val uuid: UUID

    /**
     * The name of the corresponding group
     */
    @get:JvmName("getGroupName")
    val groupName: String

    /**
     * The [type][GroupType] of the corresponding group
     */
    @get:JvmName("getGroupType")
    val groupType: GroupType

    /**
     * The port of the server
     */
    @get:JvmName("getPort")
    val port: Int

    /**
     * Sets the game-state of a server
     * @param gameState The new [GameState]
     */
    @JvmName("setGameState")
    fun setGameState(gameState: GameState)

    /**
     * Gets the game-state of a server
     * @return The [GameState] of the server
     */
    @JvmName("getGameState")
    fun getGameState(): GameState

    /**
     * Gets a server's online count
     * @return The online count of the server
     */
    @JvmName("getOnlineCount")
    fun getOnlineCount(): Int

    /**
     * Sends a message to the server which can be received via an event
     * @param message The message as a list
     */
    @JvmName("sendServerMessage")
    fun sendServerMessage(message: List<String>)

    /**
     * Gets the motd of the server
     * @return The motd of the server
     */
    @JvmName("getMotd")
    fun getMotd(): String

    /**
     * Sets the motd of the server
     * @param motd The new motd
     */
    @JvmName("setMotd")
    fun setMotd(motd: String)
}