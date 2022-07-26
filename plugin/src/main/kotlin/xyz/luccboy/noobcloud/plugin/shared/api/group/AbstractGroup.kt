package xyz.luccboy.noobcloud.plugin.shared.api.group

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.api.group.Group
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.api.server.Server

data class AbstractGroup(
    override val groupType: GroupType,
    override val name: String,
    override val lobby: Boolean
) : Group {
    override fun getServers(): List<Server> = NoobCloudAPI.instance.getAllServers().filter { it.groupName == name }
}