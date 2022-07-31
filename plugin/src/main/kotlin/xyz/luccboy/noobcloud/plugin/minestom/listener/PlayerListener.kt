package xyz.luccboy.noobcloud.plugin.minestom.listener

import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerJoinGamePacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerQuitGamePacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.plugin.minestom.NoobCloudMinestomPlugin
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerLoginEvent

class PlayerListener {

    companion object {
        fun hook(eventNode: EventNode<Event>) {
            eventNode.addListener(PlayerLoginEvent::class.java) { event ->
                val player: Player = event.player
                NoobCloudMinestomPlugin.instance.nettyClient.sendPacket(PlayerJoinGamePacket(NoobCloudMinestomPlugin.instance.uuid, NoobCloudMinestomPlugin.instance.group, player.uuid, player.username))

                if (NoobCloudMinestomPlugin.instance.startPlayerCount >= 0) {
                    if (MinecraftServer.getConnectionManager().onlinePlayers.size >= NoobCloudMinestomPlugin.instance.startPlayerCount) {
                        NoobCloudMinestomPlugin.instance.nettyClient.sendPacket(RequestServerStartPacket(NoobCloudMinestomPlugin.instance.group, GroupType.GAME.name))
                    }
                }
            }
            eventNode.addListener(PlayerDisconnectEvent::class.java) { event ->
                val player: Player = event.player
                NoobCloudMinestomPlugin.instance.nettyClient.sendPacket(PlayerQuitGamePacket(NoobCloudMinestomPlugin.instance.uuid, NoobCloudMinestomPlugin.instance.group, player.uuid, player.username))
            }
        }
    }

}