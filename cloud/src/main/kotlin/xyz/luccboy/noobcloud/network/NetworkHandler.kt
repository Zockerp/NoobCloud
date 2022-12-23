package xyz.luccboy.noobcloud.network

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.library.network.packets.api.group.GroupAddPacket
import xyz.luccboy.noobcloud.library.network.packets.api.messages.DistributeServerMessagePacket
import xyz.luccboy.noobcloud.library.network.packets.api.messages.ServerMessagePacket
import xyz.luccboy.noobcloud.library.network.packets.api.player.*
import xyz.luccboy.noobcloud.library.network.packets.api.server.*
import xyz.luccboy.noobcloud.library.network.packets.game.*
import xyz.luccboy.noobcloud.library.network.packets.proxy.ProxyServerStartedPacket
import xyz.luccboy.noobcloud.library.network.packets.proxy.ProxyServerStoppedPacket
import xyz.luccboy.noobcloud.library.network.packets.server.CopyServerTemplatePacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStopPacket
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.server.GameProcess
import xyz.luccboy.noobcloud.server.GameState
import xyz.luccboy.noobcloud.server.ProxyProcess
import xyz.luccboy.noobcloud.server.ServerProcess
import xyz.luccboy.noobcloud.template.GroupType
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.SocketException
import java.util.Optional

class NetworkHandler : SimpleChannelInboundHandler<Packet>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: Packet) {
        when (packet) {
            is ProxyServerStartedPacket -> {
                val proxyServerStartPacket: ProxyServerStartedPacket = packet
                NoobCloud.instance.nettyServer.proxyChannels.add(ctx.channel())
                NoobCloud.instance.nettyServer.channelsByUUID[proxyServerStartPacket.uuid] = ctx.channel()

                NoobCloud.instance.processManager.getGames().forEach { gameProcess ->
                    val gameServerRegisterPacket = GameServerRegisterPacket(gameProcess.gameData.name + "-" + gameProcess.id, gameProcess.port, gameProcess.gameData.lobby)
                    NoobCloud.instance.nettyServer.proxyChannels.writeAndFlush(gameServerRegisterPacket)
                }

                val time: Long = Optional.ofNullable(NoobCloud.instance.processManager.startTime[proxyServerStartPacket.uuid]).orElseGet { 0.toLong() }
                NoobCloud.instance.logger.info("The proxy " + proxyServerStartPacket.name + " started in " + (System.currentTimeMillis() - time) + "ms.")
                NoobCloud.instance.processManager.startTime.remove(proxyServerStartPacket.uuid)

                sendGroupsAndServers(ctx.channel())
                val proxyProcess: ProxyProcess = NoobCloud.instance.processManager.servers[proxyServerStartPacket.uuid] as ProxyProcess
                NoobCloud.instance.nettyServer.sendToAllClientsExcept(ctx.channel(), ServerAddPacket(
                    name = proxyProcess.proxyData.name + "-" + proxyProcess.id,
                    uuid = proxyProcess.uuid,
                    groupName = proxyProcess.proxyData.name,
                    groupType = GroupType.PROXY.name,
                    port = proxyProcess.port,
                    static = proxyProcess.proxyData.static,
                    gameState = GameState.PROXY.name
                ))
            }
            is ProxyServerStoppedPacket -> {
                val proxyServerStoppedPacket: ProxyServerStoppedPacket = packet
                NoobCloud.instance.processManager.stopServer(proxyServerStoppedPacket.name)
            }

            is GameServerStartedPacket -> {
                val gameServerStartPacket: GameServerStartedPacket = packet
                NoobCloud.instance.nettyServer.gameChannels.add(ctx.channel())
                NoobCloud.instance.nettyServer.channelsByUUID[gameServerStartPacket.uuid] = ctx.channel()

                val gameServerRegisterPacket = GameServerRegisterPacket(gameServerStartPacket.name, gameServerStartPacket.port, gameServerStartPacket.lobby)
                NoobCloud.instance.nettyServer.proxyChannels.writeAndFlush(gameServerRegisterPacket)

                val time: Long = Optional.ofNullable(NoobCloud.instance.processManager.startTime[gameServerStartPacket.uuid]).orElseGet { 0.toLong() }
                NoobCloud.instance.logger.info("The gameserver " + gameServerStartPacket.name + " started in " + (System.currentTimeMillis() - time) + "ms.")
                NoobCloud.instance.processManager.startTime.remove(gameServerStartPacket.uuid)

                sendGroupsAndServers(ctx.channel())
                val gameProcess: GameProcess = NoobCloud.instance.processManager.servers[gameServerStartPacket.uuid] as GameProcess
                NoobCloud.instance.nettyServer.sendToAllClientsExcept(ctx.channel(), ServerAddPacket(
                    name = gameProcess.gameData.name + "-" + gameProcess.id,
                    uuid = gameProcess.uuid,
                    groupName = gameProcess.gameData.name,
                    groupType = GroupType.GAME.name,
                    port = gameProcess.port,
                    static = gameProcess.gameData.static,
                    gameState = gameProcess.gameState.name
                ))
            }
            is GameServerStoppedPacket -> {
                val gameServerStoppedPacket: GameServerStoppedPacket = packet
                NoobCloud.instance.processManager.stopServer(gameServerStoppedPacket.name)
            }

            // API - Messages
            is DistributeServerMessagePacket -> {
                val distributeServerMessagePacket: DistributeServerMessagePacket = packet
                NoobCloud.instance.nettyServer.channelsByUUID[distributeServerMessagePacket.uuid]?.writeAndFlush(
                    ServerMessagePacket(distributeServerMessagePacket.message)
                )
            }
            // API - Player
            is PlayerJoinProxyPacket -> {
                val playerJoinProxyPacket: PlayerJoinProxyPacket = packet
                NoobCloud.instance.processManager.servers[playerJoinProxyPacket.proxyUUID]!!.onlineCount++
                NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateOnlineCountPacket(playerJoinProxyPacket.proxyUUID, NoobCloud.instance.processManager.servers[playerJoinProxyPacket.proxyUUID]!!.onlineCount))
            }
            is PlayerQuitProxyPacket -> {
                val playerQuitProxyPacket: PlayerQuitProxyPacket = packet
                NoobCloud.instance.processManager.servers[playerQuitProxyPacket.proxyUUID]!!.onlineCount--
                NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateOnlineCountPacket(playerQuitProxyPacket.proxyUUID, NoobCloud.instance.processManager.servers[playerQuitProxyPacket.proxyUUID]!!.onlineCount))
            }
            is PlayerJoinGamePacket ->  {
                val playerJoinGamePacket: PlayerJoinGamePacket = packet
                NoobCloud.instance.processManager.servers[playerJoinGamePacket.serverUUID]!!.onlineCount++
                NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateOnlineCountPacket(playerJoinGamePacket.serverUUID, NoobCloud.instance.processManager.servers[playerJoinGamePacket.serverUUID]!!.onlineCount))
                NoobCloud.instance.nettyServer.sendToAllClients(PlayerAddPacket(playerJoinGamePacket.serverUUID, playerJoinGamePacket.userUUID, playerJoinGamePacket.username))
            }
            is PlayerQuitGamePacket -> {
                val playerQuitGamePacket: PlayerQuitGamePacket = packet
                NoobCloud.instance.processManager.servers[playerQuitGamePacket.serverUUID]!!.onlineCount--
                NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateOnlineCountPacket(playerQuitGamePacket.serverUUID, NoobCloud.instance.processManager.servers[playerQuitGamePacket.serverUUID]!!.onlineCount))
                NoobCloud.instance.nettyServer.sendToAllClients(PlayerRemovePacket(playerQuitGamePacket.serverUUID))
            }
            is SendPlayerRequestPacket -> {
                val sendPlayerRequestPacket: SendPlayerRequestPacket = packet
                NoobCloud.instance.nettyServer.proxyChannels.writeAndFlush(SendPlayerPacket(sendPlayerRequestPacket.userUUID, sendPlayerRequestPacket.serverName))
            }

            // API - Server
            is RequestServerStartPacket -> {
                val requestServerStartPacket: RequestServerStartPacket = packet
                val groupType: GroupType = GroupType.valueOf(requestServerStartPacket.groupType)

                if (groupType == GroupType.PROXY) {
                    NoobCloud.instance.processManager.startProxyServer(NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.first { it.name.equals(requestServerStartPacket.groupName, true) })
                } else {
                    NoobCloud.instance.processManager.startGameServer(NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.first { it.name.equals(requestServerStartPacket.groupName, true) })
                }
            }
            is RequestServerStopPacket -> {
                val requestServerStopPacket: RequestServerStopPacket = packet
                NoobCloud.instance.processManager.stopServer(requestServerStopPacket.name)
                NoobCloud.instance.nettyServer.sendToAllClients(ServerRemovePacket(requestServerStopPacket.name, requestServerStopPacket.uuid))
                NoobCloud.instance.logger.info("The server ${requestServerStopPacket.name} is stopped.")
            }
            is SetGameStatePacket -> {
                val setGameStatePacket: SetGameStatePacket = packet
                val serverProcess: ServerProcess = NoobCloud.instance.processManager.servers[setGameStatePacket.uuid]!!
                if (serverProcess is GameProcess) {
                    serverProcess.gameState = GameState.valueOf(setGameStatePacket.gameState)
                    NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateGameStatePacket(setGameStatePacket.uuid, setGameStatePacket.gameState))
                }
            }
            is SetMotdPacket -> {
                val setMotdPacket: SetMotdPacket = packet
                NoobCloud.instance.nettyServer.sendToAllClients(ServerUpdateMotdPacket(setMotdPacket.uuid, setMotdPacket.motd))
            }
            is CopyServerTemplatePacket -> {
                val copyServerTemplatePacket: CopyServerTemplatePacket = packet
                NoobCloud.instance.templateManager.saveServerTemplate(copyServerTemplatePacket.groupName, GroupType.valueOf(copyServerTemplatePacket.groupType), copyServerTemplatePacket.serverName, copyServerTemplatePacket.static)
                NoobCloud.instance.logger.info("Saved ${copyServerTemplatePacket.serverName} as template for group ${copyServerTemplatePacket.groupType}.")
            }
        }
    }

    private fun sendGroupsAndServers(channel: Channel) = runBlocking {
        launch {
            withContext(Dispatchers.IO) {
                // Send groups
                NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.forEach { proxyData ->
                    channel.writeAndFlush(GroupAddPacket(
                        name = proxyData.name,
                        groupType = GroupType.PROXY.name,
                        lobby = false,
                        static = proxyData.static
                    ))
                }
                NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.forEach { gameData ->
                    channel.writeAndFlush(GroupAddPacket(
                        name = gameData.name,
                        groupType = GroupType.GAME.name,
                        lobby = gameData.lobby,
                        static = gameData.static
                    ))
                }
                // Send servers
                NoobCloud.instance.processManager.getProxies().forEach { proxyProcess ->
                    channel.writeAndFlush(ServerAddPacket(
                        name = proxyProcess.proxyData.name + "-" + proxyProcess.id,
                        uuid = proxyProcess.uuid,
                        groupName = proxyProcess.proxyData.name,
                        groupType = GroupType.PROXY.name,
                        port = proxyProcess.port,
                        static = proxyProcess.proxyData.static,
                        gameState = GameState.PROXY.name
                    ))
                    channel.writeAndFlush(ServerUpdateOnlineCountPacket(
                        uuid = proxyProcess.uuid,
                        playerOnlineCount = proxyProcess.onlineCount
                    ))
                }
                NoobCloud.instance.processManager.getGames().forEach { gameProcess ->
                    channel.writeAndFlush(ServerAddPacket(
                        name = gameProcess.gameData.name + "-" + gameProcess.id,
                        uuid = gameProcess.uuid,
                        groupName = gameProcess.gameData.name,
                        groupType = GroupType.GAME.name,
                        port = gameProcess.port,
                        static = gameProcess.gameData.static,
                        gameState = gameProcess.gameState.name
                    ))
                    channel.writeAndFlush(ServerUpdateOnlineCountPacket(
                        uuid = gameProcess.uuid,
                        playerOnlineCount = gameProcess.onlineCount
                    ))
                }
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        // Stopping servers improperly causes a SocketException that might irritate the user, so we won't show it
        // This might not be the perfect solution for this problem, but we will keep it temporary
        if (cause !is SocketException) {
            NoobCloud.instance.logger.error("An error occurred:", cause)
        }
    }

}