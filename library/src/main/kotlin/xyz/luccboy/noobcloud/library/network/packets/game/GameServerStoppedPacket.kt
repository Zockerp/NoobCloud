package xyz.luccboy.noobcloud.library.network.packets.game

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class GameServerStoppedPacket(
    var name: String,
    var uuid: UUID
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        name = readString(byteBuf)
        uuid = UUID.fromString(readString(byteBuf))
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(name, byteBuf)
        writeString(uuid.toString(), byteBuf)
    }
}