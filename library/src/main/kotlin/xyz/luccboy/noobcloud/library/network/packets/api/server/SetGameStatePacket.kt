package xyz.luccboy.noobcloud.library.network.packets.api.server

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.UUID

@NoArg
data class SetGameStatePacket(
    var uuid: UUID,
    var gameState: String
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        uuid = UUID.fromString(readString(byteBuf))
        gameState = readString(byteBuf)
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(uuid.toString(), byteBuf)
        writeString(gameState, byteBuf)
    }
}