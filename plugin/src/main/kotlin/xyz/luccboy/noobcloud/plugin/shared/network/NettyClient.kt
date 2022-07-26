package xyz.luccboy.noobcloud.plugin.shared.network

import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.codec.PacketDecoder
import xyz.luccboy.noobcloud.library.network.codec.PacketEncoder
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.library.network.protocol.PacketRegistry
import xyz.luccboy.noobcloud.plugin.minestom.NoobCloudMinestomPlugin
import xyz.luccboy.noobcloud.plugin.shared.api.AbstractNoobCloudAPI
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel

class NettyClient(groupType: GroupType) {

    var future: ChannelFuture
    private val eventLoopGroup: EventLoopGroup = NioEventLoopGroup()
    private val packetRegistry: PacketRegistry = PacketRegistry()

    init {
        val bootstrap: Bootstrap = Bootstrap()
            .group(eventLoopGroup)
            .channel(if (Epoll.isAvailable()) EpollSocketChannel::class.java else NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    channel.pipeline()
                        .addLast("packet-encoder", PacketEncoder(packetRegistry))
                        .addLast("packet-decoder",
                            xyz.luccboy.noobcloud.library.network.codec.PacketDecoder(packetRegistry)
                        )
                }
            }).validate()
        future = bootstrap.connect(if (groupType == GroupType.PROXY) NoobCloudVelocityPlugin.instance.address else NoobCloudMinestomPlugin.instance.address, 50000).sync()
    }

    fun sendPacket(packet: Packet) {
        future.channel().writeAndFlush(packet)
    }

    fun shutdown() {
        Thread {
            eventLoopGroup.shutdownGracefully().sync()
            future.channel().closeFuture().sync()
        }.start()
    }

}