package xyz.luccboy.noobcloud.library.network.packets.game

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf

@NoArg
data class GameServerRegisterPacket(
    var name: String,
    var port: Int,
    var lobby: Boolean
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        name = readString(byteBuf)
        port = byteBuf.readInt()
        lobby = byteBuf.readBoolean()
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(name, byteBuf)
        byteBuf.writeInt(port)
        byteBuf.writeBoolean(lobby)
    }
}