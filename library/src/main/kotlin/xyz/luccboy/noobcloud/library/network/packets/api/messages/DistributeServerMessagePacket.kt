package xyz.luccboy.noobcloud.library.network.packets.api.messages

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.UUID

@NoArg
data class DistributeServerMessagePacket(
    var uuid: UUID,
    var message: List<String>
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        uuid = UUID.fromString(readString(byteBuf))
        message = readStringArray(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(uuid.toString(), byteBuf)
        writeStringArray(message, byteBuf)
    }
}