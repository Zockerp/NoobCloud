package xyz.luccboy.noobcloud.api.events.velocity

import com.google.common.base.MoreObjects

/**
 * The Event called when the server receives a message <br>
 * This event is for Velocity plugins
 * @param message The message
 */
class ServerMessageVelocityEvent(private val message: List<String>) {

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