package xyz.luccboy.noobcloud.library.network.packets.api.player

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class PlayerQuitProxyPacket(
    var proxyUUID: UUID,
    var proxyGroupName: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        proxyUUID = UUID.fromString(readString(byteBuf))
        proxyGroupName = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(proxyUUID.toString(), byteBuf)
        writeString(proxyGroupName, byteBuf)
    }
}