package xyz.luccboy.noobcloud.plugin.velocity

import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.packets.proxy.ProxyServerStartedPacket
import xyz.luccboy.noobcloud.library.network.packets.proxy.ProxyServerStoppedPacket
import xyz.luccboy.noobcloud.plugin.velocity.commands.CloudCommand
import xyz.luccboy.noobcloud.plugin.shared.api.AbstractNoobCloudAPI
import xyz.luccboy.noobcloud.plugin.shared.database.DatabaseManager
import xyz.luccboy.noobcloud.plugin.velocity.listener.PlayerListener
import xyz.luccboy.noobcloud.plugin.shared.network.NettyClient
import xyz.luccboy.noobcloud.plugin.shared.network.NetworkHandler
import xyz.luccboy.noobcloud.plugin.velocity.listener.PingListener
import com.google.inject.Inject
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import org.slf4j.Logger
import java.util.UUID

@Plugin(id = "noobcloud", name = "NoobCloud", version = "1.0-SNAPSHOT", description = "NoobCloud-Plugin", authors = ["Luccboy"])
class NoobCloudVelocityPlugin @Inject constructor(val server: ProxyServer, val logger: Logger) {

    companion object {
        lateinit var instance: NoobCloudVelocityPlugin
            private set
    }

    init {
        instance = this
    }

    lateinit var databaseManager: DatabaseManager
    lateinit var noobCloudAPI: AbstractNoobCloudAPI
    lateinit var nettyClient: NettyClient

    val lobbyServers: MutableList<ServerInfo> = mutableListOf()

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        logger.info("\n" +
                " __    _  _______  _______  _______  _______  ___      _______  __   __  ______  \n" +
                "|  |  | ||       ||       ||  _    ||       ||   |    |       ||  | |  ||      | \n" +
                "|   |_| ||   _   ||   _   || |_|   ||       ||   |    |   _   ||  | |  ||  _    |\n" +
                "|       ||  | |  ||  | |  ||       ||       ||   |    |  | |  ||  |_|  || | |   |\n" +
                "|  _    ||  |_|  ||  |_|  ||  _   | |      _||   |___ |  |_|  ||       || |_|   |\n" +
                "| | |   ||       ||       || |_|   ||     |_ |       ||       ||       ||       |\n" +
                "|_|  |__||_______||_______||_______||_______||_______||_______||_______||______| \n" +
                "by Luccboy"
        )

        databaseManager = DatabaseManager(databaseEnabled, databaseHost, databaseUser, databasePassword, databaseName, databasePort).connect()
        nettyClient = NettyClient(GroupType.PROXY)
        noobCloudAPI = AbstractNoobCloudAPI(nettyClient, databaseManager)
        nettyClient.future.channel().pipeline().addLast("network-handler", NetworkHandler(GroupType.PROXY, nettyClient, noobCloudAPI))

        nettyClient.sendPacket(ProxyServerStartedPacket(name, uuid))

        val commandManager: CommandManager = server.commandManager
        commandManager.register(commandManager.metaBuilder("noobcloud").aliases("cloud").build(), CloudCommand())
        server.eventManager.register(this, PlayerListener())
        server.eventManager.register(this, PingListener())
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        databaseManager.disconnect()
        nettyClient.sendPacket(ProxyServerStoppedPacket(name, uuid))
        nettyClient.shutdown()
    }

    val prefix: String = System.getProperty("prefix")
    val name: String = System.getProperty("name")
    val group: String = System.getProperty("group")
    val uuid: UUID = UUID.fromString(System.getProperty("uuid"))
    val address: String = System.getProperty("address")
    val startPlayerCount: Int = System.getProperty("startPlayerCount").toInt()

    // Database
    private val databaseEnabled: Boolean = System.getProperty("databaseEnabled").toBoolean()
    private val databaseHost: String = System.getProperty("databaseHost")
    private val databasePort: Int = System.getProperty("databasePort").toInt()
    private val databaseName: String = System.getProperty("databaseName")
    private val databaseUser: String = System.getProperty("databaseUser")
    private val databasePassword: String = System.getProperty("databasePassword")

}