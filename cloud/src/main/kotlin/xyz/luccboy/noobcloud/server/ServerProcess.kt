package xyz.luccboy.noobcloud.server

import xyz.luccboy.noobcloud.config.GameData
import xyz.luccboy.noobcloud.config.ProxyData
import java.util.*

abstract class ServerProcess {
    abstract val type: ServerType
    abstract val name: String
    abstract val port: Int
    abstract val uuid: UUID
    abstract val process: Process
    abstract var onlineCount: Int
}

enum class ServerType {
    PROXY,
    GAME
}

data class ProxyProcess(
    val id: Int,
    override val port: Int,
    override val uuid: UUID,
    val proxyData: ProxyData,
    override val process: Process,
    override var onlineCount: Int
) : ServerProcess() {
    override val type: ServerType = ServerType.PROXY
    override val name: String = proxyData.name + "-$id"
}

data class GameProcess(
    val id: Int,
    override val port: Int,
    override val uuid: UUID,
    val gameData: GameData,
    override val process: Process,
    override var onlineCount: Int,
    var gameState: GameState
) : ServerProcess() {
    override val type: ServerType = ServerType.GAME
    override val name: String = gameData.name + "-$id"
}