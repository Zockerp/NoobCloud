package xyz.luccboy.noobcloud.api.events

import com.google.common.base.MoreObjects
import net.minestom.server.event.Event

/**
 * The Event called when the server receives a message
 * @param message The message
 */
class ServerMessageEvent(private val message: List<String>) : Event {

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