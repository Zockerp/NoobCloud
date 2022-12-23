package xyz.luccboy.noobcloud.library.network.packets.api.server

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class ServerAddPacket(
    var name: String,
    var uuid: UUID,
    var groupName: String,
    var groupType: String,
    var port: Int,
    var static: Boolean,
    var gameState: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        name = readString(byteBuf)
        uuid = UUID.fromString(readString(byteBuf))
        groupName = readString(byteBuf)
        groupType = readString(byteBuf)
        port = byteBuf.readInt()
        static = byteBuf.readBoolean()
        gameState = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(name, byteBuf)
        writeString(uuid.toString(), byteBuf)
        writeString(groupName, byteBuf)
        writeString(groupType, byteBuf)
        byteBuf.writeInt(port)
        byteBuf.writeBoolean(static)
        writeString(gameState, byteBuf)
    }
}