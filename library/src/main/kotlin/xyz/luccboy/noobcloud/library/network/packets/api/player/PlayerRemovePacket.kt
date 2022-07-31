package xyz.luccboy.noobcloud.library.network.packets.api.player

import xyz.luccboy.noobcloud.library.annotations.NoArg
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import io.netty.buffer.ByteBuf
import java.util.*

@NoArg
data class PlayerRemovePacket(
    var userUUID: UUID
) : Packet() {
    override fun read(byteBuf: ByteBuf) {
        userUUID = UUID.fromString(readString(byteBuf))
    }

    override fun write(byteBuf: ByteBuf) {
        writeString(userUUID.toString(), byteBuf)
    }
}