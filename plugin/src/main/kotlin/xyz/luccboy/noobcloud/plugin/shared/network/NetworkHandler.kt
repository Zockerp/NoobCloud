package xyz.luccboy.noobcloud.plugin.shared.network

import com.velocitypowered.api.proxy.server.ServerInfo
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import xyz.luccboy.noobcloud.api.events.minestom.ServerChangeGameStateMinestomEvent
import xyz.luccboy.noobcloud.api.events.minestom.ServerMessageMinestomEvent
import xyz.luccboy.noobcloud.api.events.velocity.ServerChangeGameStateVelocityEvent
import xyz.luccboy.noobcloud.api.events.velocity.ServerMessageVelocityEvent
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.api.server.GameState
import xyz.luccboy.noobcloud.library.network.packets.api.group.GroupAddPacket
import xyz.luccboy.noobcloud.library.network.packets.api.group.GroupRemovePacket
import xyz.luccboy.noobcloud.library.network.packets.api.messages.ServerMessagePacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerAddPacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.PlayerRemovePacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.SendPlayerPacket
import xyz.luccboy.noobcloud.library.network.packets.api.server.*
import xyz.luccboy.noobcloud.library.network.packets.game.GameServerRegisterPacket
import xyz.luccboy.noobcloud.library.network.packets.server.ExecuteCmdPacket
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.plugin.minestom.NoobCloudMinestomPlugin
import xyz.luccboy.noobcloud.plugin.shared.api.AbstractNoobCloudAPI
import xyz.luccboy.noobcloud.plugin.shared.api.group.AbstractGroup
import xyz.luccboy.noobcloud.plugin.shared.api.player.AbstractCloudPlayer
import xyz.luccboy.noobcloud.plugin.shared.api.server.AbstractServer
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import xyz.luccboy.noobcloud.plugin.shared.config.Messages
import java.net.InetSocketAddress

class NetworkHandler(private val groupType: GroupType, private val nettyClient: NettyClient, private val noobCloudAPI: AbstractNoobCloudAPI) : SimpleChannelInboundHandler<Packet>() {

    private val messages: Messages = if (groupType == GroupType.PROXY) NoobCloudVelocityPlugin.instance.config.messages else NoobCloudMinestomPlugin.instance.config.messages
    private val prefix: String = messages.prefix

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is GameServerRegisterPacket -> {
                val gameServerRegisterPacket: GameServerRegisterPacket = packet

                val serverInfo = ServerInfo(gameServerRegisterPacket.name, InetSocketAddress(NoobCloudVelocityPlugin.instance.address, gameServerRegisterPacket.port))
                NoobCloudVelocityPlugin.instance.server.registerServer(serverInfo)
                if (gameServerRegisterPacket.lobby) {
                    NoobCloudVelocityPlugin.instance.lobbyServers.remove(serverInfo)
                    NoobCloudVelocityPlugin.instance.lobbyServers.add(serverInfo)
                }

                NoobCloudVelocityPlugin.instance.server.allPlayers.filter { it.hasPermission("noobcloud.admin") }.forEach {
                    it.sendMessage(Component.text(messages.prefix + messages.getMessage(messages.serverStarted, gameServerRegisterPacket.name)))
                }
            }
            // cmd command from cloud console
            is ExecuteCmdPacket -> {
                val executeCmdPacket: ExecuteCmdPacket = packet
                if (groupType == GroupType.GAME) {
                    MinecraftServer.getCommandManager().execute(MinecraftServer.getCommandManager().consoleSender, executeCmdPacket.command)
                } else {
                    NoobCloudVelocityPlugin.instance.server.commandManager.executeAsync(NoobCloudVelocityPlugin.instance.server.consoleCommandSource, executeCmdPacket.command)
                }
            }

            // API - Messages
            is ServerMessagePacket -> {
                val serverMessagePacket: ServerMessagePacket = packet
                if (groupType == GroupType.GAME) {
                    MinecraftServer.getGlobalEventHandler().call(ServerMessageMinestomEvent(serverMessagePacket.message))
                } else {
                    NoobCloudVelocityPlugin.instance.server.eventManager.fireAndForget(ServerMessageVelocityEvent(serverMessagePacket.message))
                }
            }
            // API - Groups
            is GroupAddPacket -> {
                val groupAddPacket: GroupAddPacket = packet
                val group = AbstractGroup(
                    groupType = GroupType.valueOf(groupAddPacket.groupType),
                    name = groupAddPacket.name,
                    lobby = groupAddPacket.lobby,
                    static = groupAddPacket.static
                )
                noobCloudAPI.addGroup(group)
            }
            is GroupRemovePacket -> {
                val groupRemovePacket: GroupRemovePacket = packet
                noobCloudAPI.groups.removeIf { it.groupType.name == groupRemovePacket.groupType && it.name == groupRemovePacket.name }
            }
            // API - Servers
            is ServerAddPacket -> {
                val serverAddPacket: ServerAddPacket = packet
                val serverGroupType: GroupType = GroupType.valueOf(serverAddPacket.groupType)
                val server = AbstractServer(
                    nettyClient = nettyClient,
                    name = serverAddPacket.name,
                    uuid = serverAddPacket.uuid,
                    groupName = serverAddPacket.groupName,
                    groupType = serverGroupType,
                    port = serverAddPacket.port,
                    static = serverAddPacket.static,
                    serverGameState = GameState.valueOf(serverAddPacket.gameState),
                    playerOnlineCount = 0,
                    currentMotd = if (serverGroupType == GroupType.PROXY) messages.getMessage(messages.proxyMotd, serverAddPacket.name) else messages.getMessage(messages.gameMotd, serverAddPacket.name)
                )
                noobCloudAPI.addServer(server)
            }
            is ServerRemovePacket -> {
                val serverRemovePacket: ServerRemovePacket = packet
                noobCloudAPI.servers.removeIf { it.uuid == serverRemovePacket.uuid }
                if (groupType == GroupType.PROXY) {
                    NoobCloudVelocityPlugin.instance.lobbyServers.removeIf { it.name == serverRemovePacket.name }
                    NoobCloudVelocityPlugin.instance.server.unregisterServer(NoobCloudVelocityPlugin.instance.server.getServer(serverRemovePacket.name).get().serverInfo)

                    NoobCloudVelocityPlugin.instance.server.allPlayers.filter { it.hasPermission("noobcloud.admin") }.forEach {
                        it.sendMessage(Component.text(prefix + messages.getMessage(messages.serverStopped, serverRemovePacket.name)))
                    }
                }
            }
            is ServerUpdateOnlineCountPacket -> {
                val serverUpdateOnlineCountPacket: ServerUpdateOnlineCountPacket = packet
                noobCloudAPI.servers.first { it.uuid == serverUpdateOnlineCountPacket.uuid }.playerOnlineCount = serverUpdateOnlineCountPacket.playerOnlineCount
            }
            is ServerUpdateGameStatePacket -> {
                val serverUpdateGameStatePacket: ServerUpdateGameStatePacket = packet
                val gameState: GameState = GameState.valueOf(serverUpdateGameStatePacket.gameState)
                noobCloudAPI.servers.first { it.uuid == serverUpdateGameStatePacket.uuid }.serverGameState = gameState

                if (groupType == GroupType.GAME) {
                    MinecraftServer.getGlobalEventHandler().call(ServerChangeGameStateMinestomEvent(gameState))
                } else {
                    NoobCloudVelocityPlugin.instance.server.eventManager.fireAndForget(ServerChangeGameStateVelocityEvent(gameState))
                }
            }
            is ServerUpdateMotdPacket -> {
                val serverUpdateMotdPacket: ServerUpdateMotdPacket = packet
                noobCloudAPI.servers.first { it.uuid == serverUpdateMotdPacket.uuid }.currentMotd = serverUpdateMotdPacket.motd
            }
            // API - Player
            is PlayerAddPacket -> {
                val playerAddPacket: PlayerAddPacket = packet
                val player = AbstractCloudPlayer(
                    username = playerAddPacket.username,
                    uuid = playerAddPacket.userUUID,
                    serverUUID = playerAddPacket.serverUUID,
                    noobCloudAPI = noobCloudAPI
                )
                noobCloudAPI.addPlayer(player)
            }
            is PlayerRemovePacket -> {
                val playerRemovePacket: PlayerRemovePacket = packet
                noobCloudAPI.players.removeIf { it.uuid == playerRemovePacket.userUUID }
            }
            is SendPlayerPacket -> {
                val sendPlayerPacket: SendPlayerPacket = packet
                NoobCloudVelocityPlugin.instance.server.getServer(sendPlayerPacket.serverName).ifPresent { server ->
                    NoobCloudVelocityPlugin.instance.server.getPlayer(sendPlayerPacket.userUUID).ifPresent { it.createConnectionRequest(server).fireAndForget() }
                }
            }
        }
    }

}