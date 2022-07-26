package xyz.luccboy.noobcloud.api.events

import xyz.luccboy.noobcloud.api.server.GameState
import com.google.common.base.MoreObjects
import net.minestom.server.event.Event

/**
 * The Event called when the gamestate of a server was changed
 * @param gameState The updated [GameState]
 */
class ServerChangeGameStateEvent(private val gameState: GameState) : Event {

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