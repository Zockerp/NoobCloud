package xyz.luccboy.noobcloud.api.events.minestom

import com.google.common.base.MoreObjects
import net.minestom.server.event.Event

/**
 * The Event called when the server receives a message <br>
 * This event is for Minestom extensions
 * @param message The message
 */
class ServerMessageMinestomEvent(private val message: List<String>) : Event {

    /**
     * The received message
     * @return The message
     */
    @JvmName("getMessage")
    fun getMessage(): List<String> = message

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("message", message)
            .toString()
    }
}