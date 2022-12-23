package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.config.GameData
import xyz.luccboy.noobcloud.config.ProxyData
import xyz.luccboy.noobcloud.console.Command

class RestartCommand : Command {
    override val name: String = "restart"
    override val description: String = "Restarts a whole group"
    override val aliases: Array<String> = arrayOf("restart")
    override val completer: Node = node(name, *aliases, node("<group>"))

    override fun execute(args: Array<String>) {
        if (args.size == 1) {
            val displayName: String = args[0]

            val proxyData: ProxyData? = NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.find { it.name.equals(displayName, true) }
            if (proxyData != null) {
                NoobCloud.instance.logger.info("The group " + proxyData.name + " will be restarted.")

                NoobCloud.instance.processManager.getProxies().filter { it.proxyData.name == proxyData.name }.forEach {
                    NoobCloud.instance.processManager.stopServer(it.name)
                }
                NoobCloud.instance.processManager.resetProxyId(proxyData)
                repeat(proxyData.minAmount) {
                    NoobCloud.instance.processManager.startProxyServer(proxyData)
                }
            } else {
                val gameData: GameData? = NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.find { it.name.equals(displayName, true) }
                if (gameData != null) {
                    NoobCloud.instance.logger.info("The group " + gameData.name + " will be restarted.")

                    NoobCloud.instance.processManager.getGames().filter { it.gameData.name == gameData.name }.forEach {
                        NoobCloud.instance.processManager.stopServer(it.name)
                    }
                    NoobCloud.instance.processManager.resetGameId(gameData)
                    repeat(gameData.minAmount) {
                        NoobCloud.instance.processManager.startGameServer(gameData)
                    }
                } else {
                    NoobCloud.instance.logger.error("There is no group with this name!")
                }
            }
        } else {
            sendHelp()
        }
    }

    private fun sendHelp() {
        NoobCloud.instance.logger.info("Please use /restart <group>")
    }
}