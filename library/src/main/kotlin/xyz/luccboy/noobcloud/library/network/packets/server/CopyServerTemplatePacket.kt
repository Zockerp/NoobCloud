package xyz.luccboy.noobcloud.library.network.packets.server

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class CopyServerTemplatePacket(
    var groupName: String,
    var groupType: String,
    var serverName: String,
    var static: Boolean
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        groupName = readString(byteBuf)
        groupType = readString(byteBuf)
        serverName = readString(byteBuf)
        static = byteBuf.readBoolean()
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(groupName, byteBuf)
        writeString(groupType, byteBuf)
        writeString(serverName, byteBuf)
        byteBuf.writeBoolean(static)
    }
}