package xyz.luccboy.noobcloud.library.network.protocol

import xyz.luccboy.noobcloud.library.network.packets.api.group.GroupAddPacket
import xyz.luccboy.noobcloud.library.network.packets.api.group.GroupRemovePacket
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
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import kotlin.reflect.KClass

class PacketRegistry {

    private val packets: BiMap<Int, KClass<out Packet>> = HashBiMap.create()

    private fun registerPacket(packetClass: KClass<out Packet>, packetId: Int) = packets.put(packetId, packetClass)
    fun containsPacketId(packetId: Int) = packets.containsKey(packetId)
    fun getPacketClassById(packetId: Int): KClass<out Packet> = packets[packetId]!!
    fun getIdByPacketClass(packetClass: KClass<out Packet>): Int = packets.inverse()[packetClass]!!

    init {
        // GameServer-Packets
        registerPacket(GameServerRegisterPacket::class, 0)
        registerPacket(GameServerStartedPacket::class, 1)
        registerPacket(GameServerStoppedPacket::class, 2)
        // ProxyServer-Packets
        registerPacket(ProxyServerStartedPacket::class, 3)
        registerPacket(ProxyServerStoppedPacket::class, 4)
        // Server-Packets
        registerPacket(CopyServerTemplatePacket::class, 5)
        registerPacket(RequestServerStartPacket::class, 6)
        registerPacket(RequestServerStopPacket::class, 7)
        // API-Packets
        registerPacket(GroupAddPacket::class, 8)
        registerPacket(GroupRemovePacket::class, 9)
        registerPacket(DistributeServerMessagePacket::class, 10)
        registerPacket(ServerMessagePacket::class, 11)
        registerPacket(PlayerAddPacket::class, 12)
        registerPacket(PlayerJoinGamePacket::class, 13)
        registerPacket(PlayerJoinProxyPacket::class, 14)
        registerPacket(PlayerQuitGamePacket::class, 15)
        registerPacket(PlayerQuitProxyPacket::class, 16)
        registerPacket(PlayerRemovePacket::class, 17)
        registerPacket(SendPlayerPacket::class, 18)
        registerPacket(SendPlayerRequestPacket::class, 19)
        registerPacket(ServerAddPacket::class, 20)
        registerPacket(ServerRemovePacket::class, 21)
        registerPacket(ServerUpdateGameStatePacket::class, 22)
        registerPacket(ServerUpdateMotdPacket::class, 23)
        registerPacket(ServerUpdateOnlineCountPacket::class, 24)
        registerPacket(SetGameStatePacket::class, 25)
        registerPacket(SetMotdPacket::class, 26)
    }

}