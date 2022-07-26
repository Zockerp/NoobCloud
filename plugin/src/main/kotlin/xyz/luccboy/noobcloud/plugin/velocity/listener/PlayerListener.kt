package xyz.luccboy.noobcloud.plugin.velocity.listener

import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerJoinProxyPacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerQuitProxyPacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.event.player.ServerPreConnectEvent
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import java.util.*

class PlayerListener {

    // Player-Join
    @Subscribe
    fun onServerPreConnect(event: ServerPreConnectEvent) {
        val player: Player = event.player
        val freeLobby: Optional<RegisteredServer> = getFreeLobby()

        if (event.previousServer == null) {
            freeLobby.ifPresentOrElse({ server ->
                event.result = ServerPreConnectEvent.ServerResult.allowed(server)
            }, {
                player.disconnect(Component.text("§cEs konnte kein passender Server gefunden werden!"))
            })
        }
    }

    @Subscribe
    fun onServerPostConnect(event: ServerPostConnectEvent) {
        if (event.previousServer == null) {
            NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerJoinProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
            NoobCloudVelocityPlugin.instance.databaseManager.insertPlayer(event.player)

            if (NoobCloudVelocityPlugin.instance.startPlayerCount >= 0) {
                if (event.player.currentServer.get().server.playersConnected.size >= NoobCloudVelocityPlugin.instance.startPlayerCount) {
                    NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(RequestServerStartPacket(
                        NoobCloudVelocityPlugin.instance.group, GroupType.PROXY.name))
                }
            }
        }
    }

    // Player-Quit
    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player: Player = event.player
        if (player.currentServer.isPresent) {
            NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(PlayerQuitProxyPacket(NoobCloudVelocityPlugin.instance.uuid, NoobCloudVelocityPlugin.instance.group))
        }
    }

    // Player-Kick
    @Subscribe
    fun onServerKick(event: KickedFromServerEvent) {
        val player: Player = event.player
        if (!player.currentServer.isPresent) return

        val freeLobby: Optional<RegisteredServer> = getFreeLobby()
        freeLobby.ifPresentOrElse({ server ->
            event.result = RedirectPlayer.create(server)
        }, {
            player.disconnect(Component.text("§cCould not connect to a default or fallback server."))
        })
    }

    private fun getFreeLobby(): Optional<RegisteredServer> = runBlocking(Dispatchers.IO) {
        var maxPlayers = 0
        NoobCloudVelocityPlugin.instance.lobbyServers.forEach { serverInfo ->
            val onlinePlayers: Int = NoobCloudVelocityPlugin.instance.server.getServer(serverInfo.name).get().playersConnected.size
            if (onlinePlayers > maxPlayers) maxPlayers = onlinePlayers
        }
        NoobCloudVelocityPlugin.instance.lobbyServers.forEach { serverInfo ->
            val server: RegisteredServer = NoobCloudVelocityPlugin.instance.server.getServer(serverInfo.name).get()
            if (server.playersConnected.size <= maxPlayers) return@runBlocking Optional.of(server)
        }

        return@runBlocking Optional.empty()
    }

}