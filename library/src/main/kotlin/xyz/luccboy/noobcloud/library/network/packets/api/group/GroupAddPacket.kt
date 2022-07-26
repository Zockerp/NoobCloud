package xyz.luccboy.noobcloud.library.network.packets.api.group

import xyz.luccboy.noobcloud.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class GroupAddPacket(
    var groupType: String,
    var name: String,
    var lobby: Boolean
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        groupType = readString(byteBuf)
        name = readString(byteBuf)
        lobby = byteBuf.readBoolean()
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(groupType, byteBuf)
        writeString(name, byteBuf)
        byteBuf.writeBoolean(lobby)
    }
}