package xyz.luccboy.noobcloud.plugin.shared.api

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.api.group.Group
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.api.player.CloudPlayer
import xyz.luccboy.noobcloud.api.server.Server
import xyz.luccboy.noobcloud.library.network.packets.api.player.SendPlayerRequestPacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.plugin.shared.api.group.AbstractGroup
import xyz.luccboy.noobcloud.plugin.shared.api.player.AbstractCloudPlayer
import xyz.luccboy.noobcloud.plugin.shared.api.server.AbstractServer
import xyz.luccboy.noobcloud.plugin.shared.database.DatabaseManager
import xyz.luccboy.noobcloud.plugin.shared.network.NettyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.*

class AbstractNoobCloudAPI(private val nettyClient: NettyClient, private val databaseManager: DatabaseManager) : NoobCloudAPI() {

    val players: MutableList<AbstractCloudPlayer> = mutableListOf()
    fun addPlayer(player: AbstractCloudPlayer) {
        if (players.contains(player)) players.remove(player)
        players.add(player)
    }
    override fun getAllPlayers(): List<CloudPlayer> = players
    override fun getCloudPlayer(uuid: UUID): Optional<CloudPlayer> = runBlocking(Dispatchers.IO) {
        if (players.any { it.uuid == uuid }) {
            return@runBlocking Optional.of(players.first { it.uuid == uuid })
        }
        return@runBlocking Optional.empty()
    }
    override fun getCloudPlayer(username: String): Optional<CloudPlayer> = runBlocking(Dispatchers.IO) {
        if (players.any { it.username == username }) {
            return@runBlocking Optional.of(players.first { it.username == username })
        }
        return@runBlocking Optional.empty()
    }
    override fun connectCloudPlayer(cloudPlayer: CloudPlayer, serverName: String) {
        nettyClient.sendPacket(SendPlayerRequestPacket(cloudPlayer.uuid, serverName))
    }

    override fun getUsernameByUUID(uuid: UUID): Optional<String> = databaseManager.getNameByUUID(uuid)
    override fun getUUIDByUsername(username: String): Optional<UUID> = databaseManager.getUUIDByName(username)

    val groups: MutableList<AbstractGroup> = mutableListOf()
    fun addGroup(group: AbstractGroup) {
        if (groups.contains(group)) groups.remove(group)
        groups.add(group)
    }
    override fun getAllGroups(): List<Group> = groups
    override fun getAllProxyGroups(): List<Group> = groups.filter { it.groupType == GroupType.PROXY }
    override fun getAllGameGroups(): List<Group> = groups.filter { it.groupType == GroupType.GAME }
    override fun getGroup(name: String): Optional<Group> = runBlocking(Dispatchers.IO) {
        val group: Group? = getAllGroups().find { it.name == name }
        return@runBlocking if (group == null) Optional.empty() else Optional.of(group)
    }
    override fun getGroupOnlineCount(groupName: String): Int = runBlocking(Dispatchers.IO) {
        var onlineCount = 0
        getAllServers().filter { it.groupName == groupName }.forEach { onlineCount += it.getOnlineCount() }

        return@runBlocking onlineCount
    }

    val servers: MutableList<AbstractServer> = mutableListOf()
    fun addServer(server: AbstractServer) {
        if (servers.contains(server)) servers.remove(server)
        servers.add(server)
    }
    override fun getAllServers(): List<Server> = servers
    override fun getAllProxyServers(): List<Server> = servers.filter { it.groupType == GroupType.PROXY }
    override fun getAllGameServers(): List<Server> = servers.filter { it.groupType == GroupType.GAME }
    override fun getServerByUUID(uuid: UUID): Optional<Server> = runBlocking(Dispatchers.IO) {
        val server: Server? = getAllServers().find { it.uuid == uuid }
        return@runBlocking if (server == null) Optional.empty() else Optional.of(server)
    }
    override fun getServerByName(name: String): Optional<Server> = runBlocking(Dispatchers.IO) {
        val server: Server? = servers.find { it.name == name }
        return@runBlocking if (server == null) Optional.empty() else Optional.of(server)
    }

    override fun startServer(groupName: String) {
        getGroup(groupName).ifPresent{
            nettyClient.sendPacket(RequestServerStartPacket(it.name, it.groupType.name))
        }
    }

    override fun stopServer(name: String) {
        getServerByName(name).ifPresent {
            nettyClient.sendPacket(RequestServerStartPacket(it.name, GroupType.PROXY.name))
        }
    }

    override fun getMotd(serverName: String): String = servers.first { it.name == serverName }.getMotd()

}