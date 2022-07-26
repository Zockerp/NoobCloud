package xyz.luccboy.noobcloud.plugin.shared.api.player

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.api.player.CloudPlayer
import xyz.luccboy.noobcloud.api.server.Server
import java.util.*

data class AbstractCloudPlayer(
    override val username: String,
    override val uuid: UUID,
    var serverUUID: UUID,
    var noobCloudAPI: NoobCloudAPI
) : CloudPlayer {
    override fun getServer(): Optional<Server> = noobCloudAPI.getServerByUUID(serverUUID)
    override fun connect(serverName: String) = noobCloudAPI.connectCloudPlayer(this, serverName)
}