package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command

class ServerCommand : Command {
    override val name: String = "server"
    override val description: String = "Starts or stops a server"
    override val aliases: Array<String> = arrayOf("server")
    override val completer: Node = node(name, *aliases, node("start", node("<group>")), node("stop", node("<display-id>")))

    override fun execute(args: Array<String>) {
        if (args.size == 2) {
            if (args[0].equals("start", true)) {
                val groupName: String = args[1]

                if (NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.any { it.name.equals(groupName, true) }) {
                    NoobCloud.instance.logger.info("A proxy of the group $groupName will be started.")
                    NoobCloud.instance.processManager.startProxyServer(NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.first { it.name.equals(groupName, true) })
                } else if (NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.any { it.name.equals(groupName, true) }) {
                    NoobCloud.instance.logger.info("A gameserver of the group $groupName will be started.")
                    NoobCloud.instance.processManager.startGameServer(NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.first { it.name.equals(groupName, true) })
                } else {
                    NoobCloud.instance.logger.error("There is no group with this name!")
                }
            } else if (args[0].equals("stop", true)) {
                try {
                    if (!NoobCloud.instance.processManager.servers.values.any { it.name == args[1] }) {
                        NoobCloud.instance.logger.error("There is no server with this name!")
                        return
                    }

                    NoobCloud.instance.processManager.stopServer(args[1])
                    NoobCloud.instance.logger.info("The server ${args[1]} was stopped.")
                } catch (indexOutOfBoundsException: IndexOutOfBoundsException) {
                    NoobCloud.instance.logger.error("This server does not exist!")
                } catch (numberFormatException: NumberFormatException) {
                    NoobCloud.instance.logger.error("This server does not exist!")
                }
            } else {
                sendHelp()
            }
        } else {
            sendHelp()
        }
    }

    private fun sendHelp() {
        NoobCloud.instance.logger.info("Please use /server start <group>")
        NoobCloud.instance.logger.info("Please use /server stop <display-id>")
    }
}