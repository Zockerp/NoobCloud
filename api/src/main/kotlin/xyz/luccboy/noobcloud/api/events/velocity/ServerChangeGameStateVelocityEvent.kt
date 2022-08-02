package xyz.luccboy.noobcloud.api.events.velocity

import xyz.luccboy.noobcloud.api.server.GameState
import com.google.common.base.MoreObjects

/**
 * The Event called when the gamestate of a server was changed <br>
 * This event is for Velocity plugins
 * @param gameState The updated [GameState]
 */
class ServerChangeGameStateVelocityEvent(private val gameState: GameState) {

    /**
     * The new gamestate
     * @return The updated [GameState]
     */
    @JvmName("getGameState")
    fun getGameState(): GameState = gameState

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("gameState", gameState)
            .toString()
    }
}