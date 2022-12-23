package xyz.luccboy.noobcloud.plugin.velocity.commands

import xyz.luccboy.noobcloud.api.NoobCloudAPI
import xyz.luccboy.noobcloud.api.group.Group
import xyz.luccboy.noobcloud.api.group.GroupType
import xyz.luccboy.noobcloud.api.server.Server
import xyz.luccboy.noobcloud.library.network.packets.server.CopyServerTemplatePacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStartPacket
import xyz.luccboy.noobcloud.library.network.packets.server.RequestServerStopPacket
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.command.SimpleCommand.Invocation
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import xyz.luccboy.noobcloud.plugin.shared.config.Messages
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

class CloudCommand : SimpleCommand {

    private val messages: Messages = NoobCloudVelocityPlugin.instance.config.messages
    private val prefix: String = messages.prefix

    override fun execute(invocation: Invocation) {
        val sender: CommandSource = invocation.source()
        val arguments: Array<String> = invocation.arguments()

        if (sender is Player && !sender.hasPermission("noobcloud.admin")) {
            //sender.sendMessage(Component.text("$prefix§7This server is using §bNoobCloud §7by §bLuccboy§8."))
            //return
        }

        if (arguments.size == 1) {
            if (arguments[0].equals("listGroups", true)) {
                sender.sendMessage(Component.text(prefix))
                sender.sendMessage(Component.text("${prefix}${messages.listGroupsProxyHeading}"))
                val proxyGroups: List<Group> = NoobCloudAPI.instance.getAllProxyGroups()
                proxyGroups.forEachIndexed { index, group ->
                    val char: String = if (index == proxyGroups.size.dec()) messages.listGroupsPrefixLast else messages.listGroupsPrefix
                    sender.sendMessage(Component.text(prefix + char + messages.getMessage(messages.listGroupsMessage, group.name, group.groupType.name.lowercase().replaceFirstChar(Char::titlecase), NoobCloudAPI.instance.getGroupOnlineCount(group.name).toString())))
                }
                sender.sendMessage(Component.text("${prefix}${messages.listGroupsGameHeading}"))
                val gameGroups: List<Group> = NoobCloudAPI.instance.getAllGameGroups()
                gameGroups.forEachIndexed { index, group ->
                    val char: String = if (index == gameGroups.size.dec()) messages.listGroupsPrefixLast else messages.listGroupsPrefix
                    sender.sendMessage(Component.text(prefix + char + messages.getMessage(messages.listGroupsMessage, group.name, group.groupType.name.lowercase().replaceFirstChar(Char::titlecase), NoobCloudAPI.instance.getGroupOnlineCount(group.name).toString())))
                }
                sender.sendMessage(Component.text(prefix))
            } else if (arguments[0].equals("listServers", true)) {
                sender.sendMessage(Component.text(prefix))
                sender.sendMessage(Component.text("${prefix}${messages.listServersProxyHeading}"))
                val proxies: List<Server> = NoobCloudAPI.instance.getAllProxyServers()
                proxies.forEachIndexed { index, server ->
                    val char: String = if (index == proxies.size.dec()) messages.listServersPrefixLast else messages.listServersPrefix
                    sender.sendMessage(Component.text(prefix + char + messages.getMessage(messages.listServersMessage, server.name, server.groupName, server.getOnlineCount().toString())))
                }
                sender.sendMessage(Component.text("${prefix}${messages.listServersGameHeading}"))
                val games: List<Server> = NoobCloudAPI.instance.getAllGameServers()
                games.forEachIndexed { index, server ->
                    val char: String = if (index == games.size.dec()) messages.listServersPrefixLast else messages.listServersPrefix
                    sender.sendMessage(Component.text(prefix + char + messages.getMessage(messages.listServersMessage, server.name, server.groupName, server.getOnlineCount().toString())))
                }
                sender.sendMessage(Component.text(prefix))
            } else if (arguments[0].equals("copy", true)) {
                if (sender is Player) {
                    NoobCloudAPI.instance.getCloudPlayer(sender.uniqueId).ifPresent { it.getServer().ifPresent { server ->
                        NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(CopyServerTemplatePacket(server.groupName, server.groupType.name, server.name, server.static))
                        sender.sendMessage(Component.text(prefix + messages.getMessage(messages.templateWillBeSaved, server.name, server.groupName)))
                    } }
                } else {
                    sender.sendMessage(Component.text("$prefix${messages.commandOnlyForPlayers}"))
                }
            } else {
                sendHelp(sender)
            }
        } else if (arguments.size == 2) {
            if (arguments[0].equals("start", true)) {
                val groupName: String = arguments[1]
                NoobCloudAPI.instance.getGroup(groupName).ifPresentOrElse({
                    NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(RequestServerStartPacket(it.name, GroupType.GAME.name))
                    sender.sendMessage(Component.text(prefix + messages.getMessage(messages.serverWillBeStarted, it.name)))
                }, {
                    NoobCloudAPI.instance.getGroup(groupName).ifPresentOrElse({
                        NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(RequestServerStartPacket(it.name, GroupType.PROXY.name))
                        sender.sendMessage(Component.text(prefix + messages.getMessage(messages.serverWillBeStarted, it.name)))
                    }, { sender.sendMessage(Component.text(prefix + messages.groupNotFound)) })
                })
            } else if (arguments[0].equals("stop", true)) {
                NoobCloudAPI.instance.getServerByName(arguments[1]).ifPresentOrElse({
                    NoobCloudVelocityPlugin.instance.nettyClient.sendPacket(RequestServerStopPacket(it.name, it.uuid))
                    sender.sendMessage(Component.text(prefix + messages.getMessage(messages.serverWillBeStopped, it.name)))
                }, { sender.sendMessage(Component.text(prefix + messages.serverNotFound)) })
            } else {
                sendHelp(sender)
            }
        } else {
            sendHelp(sender)
        }
    }

    override fun suggestAsync(invocation: Invocation): CompletableFuture<MutableList<String>> {
        val sender: CommandSource = invocation.source()
        val arguments: Array<String> = invocation.arguments()

        if (sender is Player && !sender.hasPermission("noobcloud.admin")) {
            return CompletableFuture.completedFuture(mutableListOf())
        }

        if (arguments.isEmpty()) {
            return CompletableFuture.completedFuture(mutableListOf("listGroups", "listServers", "start", "stop", "copy"))
        } else if (arguments.size == 1) {
            val suggestions: Stream<String> = listOf("listGroups", "listServers", "start", "stop").stream()
            return CompletableFuture.completedFuture(suggestions.filter { regionMatches(it, arguments[0]) }.toList())
        } else if (arguments.size == 2 && arguments[0].equals("start", true)) {
            val suggestion: Stream<String> = NoobCloudAPI.instance.getAllGroups().map { it.name }.stream()
            return CompletableFuture.completedFuture(suggestion.filter { regionMatches(it, arguments[1]) }.toList())
        } else if (arguments.size == 2 && arguments[0].equals("stop", true)) {
            val suggestion: Stream<String> = NoobCloudAPI.instance.getAllServers().map { it.name }.stream()
            return CompletableFuture.completedFuture(suggestion.filter { regionMatches(it, arguments[1]) }.toList())
        }

        return CompletableFuture.completedFuture(mutableListOf())
    }

    private fun regionMatches(suggestion: String, argument: String): Boolean {
        return suggestion.regionMatches(0, argument, 0, argument.length, ignoreCase = true)
    }

    private fun sendHelp(sender: CommandSource) {
        sender.sendMessage(Component.text(prefix))
        sender.sendMessage(Component.text("$prefix/cloud listGroups"))
        sender.sendMessage(Component.text("$prefix/cloud listServers"))
        sender.sendMessage(Component.text("$prefix/cloud start <group>"))
        sender.sendMessage(Component.text("$prefix/cloud stop <display-id>"))
        sender.sendMessage(Component.text("$prefix/cloud copy"))
        sender.sendMessage(Component.text(prefix))
    }

}