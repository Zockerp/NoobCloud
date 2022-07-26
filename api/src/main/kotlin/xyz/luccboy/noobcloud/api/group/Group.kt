package xyz.luccboy.noobcloud.api.group

import xyz.luccboy.noobcloud.api.server.Server

/**
 * Represents a server group
 */
@Suppress("INAPPLICABLE_JVM_NAME")
interface Group {
    /**
     * The [type][GroupType] of the group
     */
    @get:JvmName("getGroupType")
    val groupType: GroupType

    /**
     * The name of the group
     */
    @get:JvmName("getName")
    val name: String

    /**
     * The boolean indicating whether the group is a lobby group
     */
    @get:JvmName("isLobby")
    val lobby: Boolean

    /**
     * Gets all active servers in this group
     * @return A list of all [servers][Server]
     */
    @JvmName("getServers")
    fun getServers(): List<Server>
}