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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import java.util.*

class PlayerListener {

    // Player-Join
    @Subscribe(order = PostOrder.FIRST)
    fun onChoose(event: PlayerChooseInitialServerEvent) {
        val player: Player = event.player
        val freeLobby: Optional<RegisteredServer> = getFreeLobby(player.currentServer.map { it.serverInfo.name }.orElse(""))

        freeLobby.ifPresentOrElse({ server ->
            event.setInitialServer(server)
        }, {
            player.disconnect(Component.text("§cNo suitable server could be found!"))
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
        NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerQuitProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
    }

    // Player-Kick
    @Subscribe(order = PostOrder.FIRST)
    fun onServerKick(event: KickedFromServerEvent) {
        val player: Player = event.player

        val freeLobby: Optional<RegisteredServer> = getFreeLobby(player.currentServer.map { it.serverInfo.name }.orElse(""))
        freeLobby.ifPresentOrElse({ server ->
            event.result = RedirectPlayer.create(server)
        }, {
            player.disconnect(Component.text("§cCould not connect to a default or fallback server."))
        })
    }

    private fun getFreeLobby(currentServer: String): Optional<RegisteredServer> = runBlocking(Dispatchers.IO) {
        var maxPlayers = 0
        NoobCloudVelocityPlugin.instance.lobbyServers.forEach { serverInfo ->
            if (serverInfo.name != currentServer) {
                val onlinePlayers: Int = NoobCloudVelocityPlugin.instance.server.getServer(serverInfo.name).get().playersConnected.size
                if (onlinePlayers > maxPlayers) maxPlayers = onlinePlayers
            }
        }
        NoobCloudVelocityPlugin.instance.lobbyServers.forEach { serverInfo ->
            if (serverInfo.name != currentServer) {
                val server: RegisteredServer = NoobCloudVelocityPlugin.instance.server.getServer(serverInfo.name).get()
                if (server.playersConnected.size <= maxPlayers) return@runBlocking Optional.of(server)
            }
        }

        return@runBlocking Optional.empty()
    }

}