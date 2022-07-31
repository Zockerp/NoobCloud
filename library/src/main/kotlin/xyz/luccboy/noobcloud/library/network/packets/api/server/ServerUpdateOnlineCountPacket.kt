package xyz.luccboy.noobcloud.library.network.packets.api.server

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class ServerUpdateOnlineCountPacket(
    var uuid: UUID,
    var playerOnlineCount: Int
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        uuid = UUID.fromString(readString(byteBuf))
        playerOnlineCount = byteBuf.readInt()
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(uuid.toString(), byteBuf)
        byteBuf.writeInt(playerOnlineCount)
    }
}