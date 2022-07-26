package xyz.luccboy.noobcloud.library.network.packets.api.player

import xyz.luccboy.noobcloud.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class PlayerJoinGamePacket(
    var serverUUID: UUID,
    var serverGroupName: String,
    var userUUID: UUID,
    var username: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        serverUUID = UUID.fromString(readString(byteBuf))
        serverGroupName = readString(byteBuf)
        userUUID = UUID.fromString(readString(byteBuf))
        username = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(serverUUID.toString(), byteBuf)
        writeString(serverGroupName, byteBuf)
        writeString(userUUID.toString(), byteBuf)
        writeString(username, byteBuf)
    }
}