package xyz.luccboy.noobcloud.plugin.shared.config

import java.text.MessageFormat

data class Messages(
    val prefix: String,
    val commandOnlyForPlayers: String,

    val listGroupsProxyHeading: String,
    val listGroupsGameHeading: String,
    val listGroupsPrefix: String,
    val listGroupsPrefixLast: String,
    val listGroupsMessage: String,

    val listServersProxyHeading: String,
    val listServersGameHeading: String,
    val listServersPrefix: String,
    val listServersPrefixLast: String,
    val listServersMessage: String,

    val serverWillBeStarted: String,
    val serverStarted: String,
    val groupNotFound: String,

    val serverWillBeStopped: String,
    val serverStopped: String,
    val serverNotFound: String,

    val templateWillBeSaved: String,

    val alreadyConnectedToLobby: String,
    val noLobbyFound: String,

    val proxyMotd: String,
    val gameMotd: String
) {
    fun getMessage(message: String, vararg arguments: String) = MessageFormat.format(message, *arguments)
}