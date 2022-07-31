package xyz.luccboy.noobcloud.library.network.packets.api.group

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class GroupRemovePacket(
    var groupType: String,
    var name: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        groupType = readString(byteBuf)
        name = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(groupType, byteBuf)
        writeString(name, byteBuf)
    }
}