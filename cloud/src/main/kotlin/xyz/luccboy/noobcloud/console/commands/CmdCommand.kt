package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command
import xyz.luccboy.noobcloud.library.network.packets.server.ExecuteCmdPacket
import xyz.luccboy.noobcloud.server.ServerProcess

class CmdCommand : Command {
    override val name: String = "cmd"
    override val description: String = "Sends a command to a server"
    override val aliases: Array<String> = arrayOf("cmd")
    override val completer: Node = node(name, *aliases, node("<display-id>", node("<command>")))

    override fun execute(args: Array<String>) {
        if (args.size >= 2) {
            if (!NoobCloud.instance.processManager.servers.values.any { it.name == args[0] }) {
                NoobCloud.instance.logger.error("There is no server with this name!")
                return
            }
            val server: ServerProcess = NoobCloud.instance.processManager.getServerProcess(args[0])

            var command = ""
            args.drop(1).forEachIndexed { index, arg -> command += if (index == args.drop(1).lastIndex) arg else "$arg " }

            NoobCloud.instance.nettyServer.channelsByUUID[server.uuid]?.writeAndFlush(ExecuteCmdPacket(command))
            NoobCloud.instance.logger.info("The command was sent to " + args[0] + ".")
        } else {
            sendHelp()
        }
    }

    private fun sendHelp() {
        NoobCloud.instance.logger.info("Please use /cmd <display-id> <command>")
    }
}