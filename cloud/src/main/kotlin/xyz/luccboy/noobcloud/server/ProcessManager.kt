package xyz.luccboy.noobcloud.server

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.config.GameData
import xyz.luccboy.noobcloud.config.ProxyData
import xyz.luccboy.noobcloud.library.network.packets.api.server.ServerRemovePacket
import xyz.luccboy.noobcloud.template.GroupType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class ProcessManager {

    val servers: MutableMap<UUID, ServerProcess> = mutableMapOf()
    fun getProxies(): List<ProxyProcess> = servers.values.filter { it.type == ServerType.PROXY }.map { it as ProxyProcess }
    fun getGames(): List<GameProcess> = servers.values.filter { it.type == ServerType.GAME }.map { it as GameProcess }

    private var proxyPort: AtomicInteger = AtomicInteger(NoobCloud.instance.cloudConfig.noobCloudConfigData.config.proxyStartPort)
    private var gamePort: AtomicInteger = AtomicInteger(NoobCloud.instance.cloudConfig.noobCloudConfigData.config.gameStartPort)
    private val gameIds: MutableMap<GameData, AtomicInteger> = mutableMapOf()
    private val proxyIds: MutableMap<ProxyData, AtomicInteger> = mutableMapOf()
    val startTime: MutableMap<UUID, Long> = mutableMapOf()

    private fun is25565Free(): Boolean = !servers.values.any { it.port == 25565 }

    fun getServerProcess(name: String): ServerProcess = servers.values.first { it.name == name }
    fun resetProxyId(proxyData: ProxyData) = proxyIds.remove(proxyData)
    fun resetGameId(gameData: GameData) = gameIds.remove(gameData)

    fun startProxyServer(proxyData: ProxyData) {
        val id: Int = if (proxyIds[proxyData] == null) 1 else proxyIds[proxyData]!!.incrementAndGet()
        if (proxyIds[proxyData] == null) proxyIds[proxyData] = AtomicInteger(id)
        val port: Int = if (is25565Free()) 25565 else proxyPort.incrementAndGet()
        val uuid: UUID = UUID.randomUUID()

        NoobCloud.instance.logger.info("The proxy " + proxyData.name + "-$id will be started...")
        startTime[uuid] = System.currentTimeMillis()

        if (!NoobCloud.instance.templateManager.copyProxyTemplate(id, port, proxyData)) return

        val process: Process = ProcessBuilder(
            NoobCloud.instance.cloudConfig.noobCloudConfigData.config.javaPath,
            "-Xmx${proxyData.memory}M",
            "-Dname=" + proxyData.name + "-$id",
            "-Dgroup=" + proxyData.name,
            "-Duuid=$uuid",
            "-Daddress=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.config.address,
            "-DstartPlayerCount=" + proxyData.startPlayerCount,
            // Database
            "-DdatabaseType=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.type,
            "-DdatabaseHost=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.host,
            "-DdatabasePort=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.port,
            "-DdatabaseName=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.database,
            "-DdatabaseUser=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.username,
            "-DdatabasePassword=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.password,
            "-jar", "velocity.jar")
            .directory(File((if (!proxyData.static) GroupType.PROXY.tempPath else GroupType.PROXY.staticPath) + "/${proxyData.name}/${proxyData.name}-$id"))
            .start()

        servers[uuid] = ProxyProcess(id, port, uuid, proxyData, process, 0)
    }

    fun startGameServer(gameData: GameData) {
        val id: Int = if (gameIds[gameData] == null) 1 else gameIds[gameData]!!.incrementAndGet()
        if (gameIds[gameData] == null) gameIds[gameData] = AtomicInteger(id)
        val port: Int = if (getGames().isEmpty()) gamePort.get() else gamePort.incrementAndGet()
        val uuid: UUID = UUID.randomUUID()

        NoobCloud.instance.logger.info("The gameserver " + gameData.name + "-$id will be started...")
        startTime[uuid] = System.currentTimeMillis()

        if (!NoobCloud.instance.templateManager.copyGameTemplate(id, gameData)) return

        val process: Process = ProcessBuilder(
            NoobCloud.instance.cloudConfig.noobCloudConfigData.config.javaPath,
            "-Xmx${gameData.memory}M",
            "-Dname=" + gameData.name + "-$id",
            "-Dgroup=" + gameData.name,
            "-Duuid=$uuid",
            "-Daddress=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.config.address,
            "-Dport=$port",
            "-Dlobby=" + gameData.lobby,
            "-DstartPlayerCount=" + gameData.startPlayerCount,
            // Database
            "-DdatabaseType=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.type,
            "-DdatabaseHost=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.host,
            "-DdatabasePort=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.port,
            "-DdatabaseName=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.database,
            "-DdatabaseUser=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.username,
            "-DdatabasePassword=" + NoobCloud.instance.cloudConfig.noobCloudConfigData.playerDatabase.password,
            "-jar", "minestom.jar")
            .directory(File((if (!gameData.static) GroupType.GAME.tempPath else GroupType.GAME.staticPath) + "/${gameData.name}/${gameData.name}-$id/"))
            .start()

        servers[uuid] = GameProcess(id, port, uuid, gameData, process, 0, GameState.AVAILABLE)
    }

    fun stopServer(name: String) = runBlocking {
        val serverProcess: ServerProcess = getServerProcess(name)
        launch {
            withContext(Dispatchers.IO) {
                if (serverProcess.process.isAlive) {
                    serverProcess.process.destroy()
                    serverProcess.process.waitFor()
                }

                if (serverProcess is ProxyProcess && !serverProcess.proxyData.static) {
                    File(GroupType.PROXY.tempPath + "/${serverProcess.proxyData.name}/${serverProcess.name}").deleteRecursively()
                } else if (serverProcess is GameProcess && !serverProcess.gameData.static) {
                    File(GroupType.GAME.tempPath + "/${serverProcess.gameData.name}/${serverProcess.name}").deleteRecursively()
                }

                NoobCloud.instance.nettyServer.proxyChannels.remove(NoobCloud.instance.nettyServer.channelsByUUID[serverProcess.uuid])
                NoobCloud.instance.nettyServer.gameChannels.remove(NoobCloud.instance.nettyServer.channelsByUUID[serverProcess.uuid])
                NoobCloud.instance.nettyServer.channelsByUUID.remove(serverProcess.uuid)
                NoobCloud.instance.nettyServer.sendToAllClients(ServerRemovePacket(serverProcess.name, serverProcess.uuid))

                servers.remove(serverProcess.uuid)
            }
        }
    }

    fun startDefaultServers() {
        NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.forEach { proxyData ->
            repeat(proxyData.minAmount) {
                startProxyServer(proxyData)
            }
        }
        NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.forEach { gameData ->
            repeat(gameData.minAmount) {
                startGameServer(gameData)
            }
        }
    }

}