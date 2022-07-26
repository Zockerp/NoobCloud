package xyz.luccboy.noobcloud.plugin.minestom

import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.library.network.packets.game.GameServerStartedPacket
import xyz.luccboy.noobcloud.library.network.packets.game.GameServerStoppedPacket
import xyz.luccboy.noobcloud.plugin.minestom.listener.PingListener
import xyz.luccboy.noobcloud.plugin.minestom.listener.PlayerListener
import xyz.luccboy.noobcloud.plugin.shared.api.AbstractNoobCloudAPI
import xyz.luccboy.noobcloud.plugin.shared.database.DatabaseManager
import xyz.luccboy.noobcloud.plugin.shared.network.NettyClient
import xyz.luccboy.noobcloud.plugin.shared.network.NetworkHandler
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.extensions.Extension
import java.util.*

class NoobCloudMinestomPlugin : Extension() {

    companion object {
        lateinit var instance: NoobCloudMinestomPlugin
            private set
    }

    init {
        instance = this
    }

    lateinit var databaseManager: DatabaseManager
    lateinit var noobCloudAPI: AbstractNoobCloudAPI
    lateinit var nettyClient: NettyClient

    override fun initialize() {
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
        nettyClient = NettyClient(GroupType.GAME)
        noobCloudAPI = AbstractNoobCloudAPI(nettyClient, databaseManager)
        nettyClient.future.channel().pipeline().addLast("network-handler", NetworkHandler(GroupType.GAME, nettyClient, noobCloudAPI))

        nettyClient.sendPacket(GameServerStartedPacket(name, uuid, port, lobby))

        val globalEventHandler: GlobalEventHandler = MinecraftServer.getGlobalEventHandler()
        PlayerListener.hook(globalEventHandler)
        PingListener.hook(globalEventHandler)
    }

    override fun terminate() {
        databaseManager.disconnect()
        nettyClient.sendPacket(GameServerStoppedPacket(name, uuid))
        nettyClient.shutdown()
    }

    val prefix: String = System.getProperty("prefix")
    val name: String = System.getProperty("name")
    val group: String = System.getProperty("group")
    val uuid: UUID = UUID.fromString(System.getProperty("uuid"))
    val address: String = System.getProperty("address")
    val port: Int = System.getProperty("port").toInt()
    val lobby: Boolean = System.getProperty("lobby").toBoolean()
    val startPlayerCount: Int = System.getProperty("startPlayerCount").toInt()

    // Database
    private val databaseEnabled: Boolean = System.getProperty("databaseEnabled").toBoolean()
    private val databaseHost: String = System.getProperty("databaseHost")
    private val databasePort: Int = System.getProperty("databasePort").toInt()
    private val databaseName: String = System.getProperty("databaseName")
    private val databaseUser: String = System.getProperty("databaseUser")
    private val databasePassword: String = System.getProperty("databasePassword")

}