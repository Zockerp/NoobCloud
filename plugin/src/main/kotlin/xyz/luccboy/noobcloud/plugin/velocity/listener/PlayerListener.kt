package xyz.luccboy.noobcloud.plugin.velocity.listener

import com.velocitypowered.api.event.PostOrder
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerJoinProxyPacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerQuitProxyPacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.kyori.adventure.text.Component
import xyz.luccboy.noobcloud.plugin.shared.config.Messages
import java.util.*

class PlayerListener {

    private val messages: Messages = NoobCloudVelocityPlugin.instance.config.messages
    private val prefix: String = messages.prefix

    // Player-Join
    @Subscribe(order = PostOrder.FIRST)
    fun onChoose(event: PlayerChooseInitialServerEvent) {
        val player: Player = event.player
        val freeLobby: Optional<RegisteredServer> = NoobCloudVelocityPlugin.instance.getFreeLobby(player.currentServer.map { it.serverInfo.name }.orElse(""))

        freeLobby.ifPresentOrElse({ server ->
            event.setInitialServer(server)
        }, {
            player.disconnect(Component.text(prefix + messages.noLobbyFound))
        })
    }

    @Subscribe(order = PostOrder.FIRST)
    fun onServerPostConnect(event: ServerPostConnectEvent) {
        if (event.previousServer == null) {
            NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerJoinProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
            NoobCloudVelocityPlugin.instance.databaseManager.insertPlayer(event.player)

            if (NoobCloudVelocityPlugin.instance.startPlayerCount >= 0) {
                if (event.player.currentServer.get().server.playersConnected.size >= NoobCloudVelocityPlugin.instance.startPlayerCount) {
                    NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(RequestServerStartPacket(NoobCloudVelocityPlugin.instance.group, GroupType.PROXY.name))
                }
            }
        }
    }

    // Player-Quit
    @Subscribe(order = PostOrder.FIRST)
    fun onDisconnect(event: DisconnectEvent) {
        if (event.player.currentServer.isPresent) NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerQuitProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
    }

    // Player-Kick
    @Subscribe(order = PostOrder.FIRST)
    fun onServerKick(event: KickedFromServerEvent) {
        val player: Player = event.player

        val freeLobby: Optional<RegisteredServer> = NoobCloudVelocityPlugin.instance.getFreeLobby(player.currentServer.map { it.serverInfo.name }.orElse(""))
        freeLobby.ifPresentOrElse({ server ->
            if (player.currentServer.isPresent) event.result = RedirectPlayer.create(server)
        }, {
            player.disconnect(Component.text(prefix + messages.noLobbyFound))
            NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerQuitProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
        })
    }

}