package xyz.luccboy.noobcloud.library.network.packets.api.messages

import xyz.luccboy.noobcloud.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class ServerMessagePacket(
    var message: List<String>
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        message = readStringArray(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeStringArray(message, byteBuf)
    }
}