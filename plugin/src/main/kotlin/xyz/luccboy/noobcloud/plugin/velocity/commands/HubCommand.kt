package xyz.luccboy.noobcloud.plugin.velocity.commands

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.command.SimpleCommand.Invocation
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.kyori.adventure.text.Component
import xyz.luccboy.noobcloud.plugin.shared.config.Messages
import xyz.luccboy.noobcloud.plugin.velocity.NoobCloudVelocityPlugin
import java.util.*

class HubCommand : SimpleCommand {

    private val messages: Messages = NoobCloudVelocityPlugin.instance.config.messages
    private val prefix: String = messages.prefix

    override fun execute(invocation: Invocation) {
        val sender: CommandSource = invocation.source()

        if (sender !is Player) return

        if (sender.currentServer.isPresent) {
            if (NoobCloudVelocityPlugin.instance.lobbyServers.contains(sender.currentServer.get().serverInfo)) {
                sender.sendMessage(Component.text(prefix + messages.alreadyConnectedToLobby))
                return
            }
        }

        val freeLobby: Optional<RegisteredServer> = NoobCloudVelocityPlugin.instance.getFreeLobby(sender.currentServer.map { it.serverInfo.name }.orElse(""))

        freeLobby.ifPresentOrElse({ server ->
            sender.createConnectionRequest(server).fireAndForget()
        }, {
            sender.sendMessage(Component.text(prefix + messages.noLobbyFound))
        })
    }

}