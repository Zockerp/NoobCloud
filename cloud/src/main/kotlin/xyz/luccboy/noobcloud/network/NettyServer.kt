package xyz.luccboy.noobcloud.network

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.library.network.codec.PacketDecoder
import xyz.luccboy.noobcloud.library.network.codec.PacketEncoder
import xyz.luccboy.noobcloud.library.network.protocol.Packet
import xyz.luccboy.noobcloud.library.network.protocol.PacketRegistry
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.group.ChannelGroup
import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.*

class NettyServer {

    private var future: ChannelFuture
    private val masterGroup: EventLoopGroup = NioEventLoopGroup()
    private val workerGroup: EventLoopGroup = NioEventLoopGroup()
    private val packetRegistry = PacketRegistry()

    val proxyChannels: ChannelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    val gameChannels: ChannelGroup = DefaultChannelGroup(GlobalEventExecutor.INSTANCE)
    val channelsByUUID: MutableMap<UUID, Channel> = mutableMapOf()

    fun sendToAllClients(packet: Packet) {
        proxyChannels.writeAndFlush(packet)
        gameChannels.writeAndFlush(packet)
    }

    fun sendToAllClientsExcept(exception: Channel, packet: Packet) {
        proxyChannels.writeAndFlush(packet) { it != exception }
        gameChannels.writeAndFlush(packet) { it != exception }
    }

    init {
        val serverBootstrap: ServerBootstrap = ServerBootstrap()
            .group(masterGroup, workerGroup)
            .channel(NioServerSocketChannel::class.java)
            .option(ChannelOption.SO_BACKLOG,128)
            .childOption(ChannelOption.SO_KEEPALIVE,true)
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(channel: Channel) {
                    channel.pipeline()
                        .addLast("packet-encoder", PacketEncoder(packetRegistry))
                        .addLast("packet-decoder", PacketDecoder(packetRegistry))
                        .addLast("network-handler", NetworkHandler())
                }
            }).validate()
        future = serverBootstrap.bind(NoobCloud.instance.cloudConfig.noobCloudConfigData.config.address, NoobCloud.instance.cloudConfig.noobCloudConfigData.config.port).sync()
    }

    fun shutdown() {
        Thread {
            masterGroup.shutdownGracefully().sync()
            workerGroup.shutdownGracefully().sync()
            future.channel().closeFuture().sync()
        }.start()
    }

}