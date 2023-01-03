package xyz.luccboy.noobcloud.library.network.packets.server

import io.netty.buffer.ByteBuf
import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet

@NoArg
class ExecuteCmdPacket(
    var command: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        command = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(command, byteBuf)
    }

}