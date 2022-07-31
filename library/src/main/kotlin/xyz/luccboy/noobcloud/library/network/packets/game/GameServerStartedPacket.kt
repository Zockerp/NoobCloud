package xyz.luccboy.noobcloud.library.network.packets.game

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.UUID

@NoArg
data class GameServerStartedPacket(
    var name: String,
    var uuid: UUID,
    var port: Int,
    var lobby: Boolean
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        name = readString(byteBuf)
        uuid = UUID.fromString(readString(byteBuf))
        port = byteBuf.readInt()
        lobby = byteBuf.readBoolean()
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(name, byteBuf)
        writeString(uuid.toString(), byteBuf)
        byteBuf.writeInt(port)
        byteBuf.writeBoolean(lobby)
    }
}