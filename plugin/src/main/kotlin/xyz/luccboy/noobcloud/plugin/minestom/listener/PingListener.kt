package xyz.luccboy.noobcloud.plugin.minestom.listener

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.plugin.minestom.NoobCloudMinestomPlugin
import net.kyori.adventure.text.Component
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.server.ServerListPingEvent

class PingListener {

    companion object {
        fun hook(eventNode: EventNode<Event>) {
            eventNode.addListener(ServerListPingEvent::class.java) { event ->
                val motd: String = NoobCloudAPI.instance.getMotd(NoobCloudMinestomPlugin.instance.name)
                event.responseData.description = Component.text(motd)
            }
        }
    }

}