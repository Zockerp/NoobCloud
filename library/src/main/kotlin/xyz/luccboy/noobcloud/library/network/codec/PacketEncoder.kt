package xyz.luccboy.noobcloud.library.network.codec

import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.library.network.protocol.PacketRegistry
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder(private val packetRegistry: PacketRegistry) : MessageToByteEncoder<Packet>() {

    override fun encode(ctx: ChannelHandlerContext, packet: Packet, output: ByteBuf) {
        val packetId: Int = packetRegistry.getIdByPacketClass(packet::class)

        output.writeInt(packetId)
        packet.write(output)
    }

}