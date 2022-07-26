package xyz.luccboy.noobcloud.plugin.shared.api.server

import xyz.luccboy.noobcloud.api.server.GameState
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.api.server.Server
import xyz.luccboy.noobcloud.library.network.packets.api.messages.DistributeServerMessagePacket
import xyz.luccboy.noobcloud.library.network.packets.api.server.SetGameStatePacket
import xyz.luccboy.noobcloud.library.network.packets.api.server.SetMotdPacket
import xyz.luccboy.noobcloud.plugin.shared.network.NettyClient
import java.util.*

data class AbstractServer(
    val nettyClient: NettyClient,
    override val name: String,
    override val uuid: UUID,
    override val groupName: String,
    override val groupType: GroupType,
    override val port: Int,
    var serverGameState: GameState,
    var playerOnlineCount: Int,
    var currentMotd: String
) : Server {
    override fun getOnlineCount(): Int = playerOnlineCount
    override fun setGameState(gameState: GameState) = nettyClient.sendPacket(SetGameStatePacket(uuid, gameState.name))
    override fun getGameState(): GameState = serverGameState

    override fun sendServerMessage(message: List<String>) {
        nettyClient.sendPacket(DistributeServerMessagePacket(uuid, message))
    }
    override fun getMotd(): String = currentMotd
    override fun setMotd(motd: String) = nettyClient.sendPacket(SetMotdPacket(uuid, motd))
}