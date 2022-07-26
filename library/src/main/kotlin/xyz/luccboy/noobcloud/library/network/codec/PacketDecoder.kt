package xyz.luccboy.noobcloud.library.network.codec

import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.library.network.protocol.PacketRegistry
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.handler.codec.DecoderException
import kotlin.reflect.KClass

class PacketDecoder(private val packetRegistry: PacketRegistry) : ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        if (!ctx.channel().isActive) {
            input.skipBytes(input.readableBytes())
            return
        }

        val packetId: Int = input.readInt()
        if (!packetRegistry.containsPacketId(packetId)) throw DecoderException("Ungültige Packet-ID: $packetId")

        val packetClass: KClass<out Packet> = packetRegistry.getPacketClassById(packetId)
        val packet: Packet = packetClass.java.getDeclaredConstructor().newInstance()
        packet.read(input)

        output.add(packet)
    }

}