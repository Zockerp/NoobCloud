package xyz.luccboy.noobcloud.library.network.packets.server

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class RequestServerStartPacket(
    var groupName: String,
    var groupType: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        groupName = readString(byteBuf)
        groupType = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(groupName, byteBuf)
        writeString(groupType, byteBuf)
    }
}