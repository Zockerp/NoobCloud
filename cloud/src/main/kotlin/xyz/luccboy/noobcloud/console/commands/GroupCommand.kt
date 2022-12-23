package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.config.GameData
import xyz.luccboy.noobcloud.config.ProxyData
import xyz.luccboy.noobcloud.console.Command
import xyz.luccboy.noobcloud.template.GroupType

class GroupCommand : Command {
    override val name: String = "group"
    override val description: String = "Creates, deletes or edits a group"
    override val aliases: Array<String> = arrayOf("group")
    override val completer: Node = node(name, *aliases,
        node("create",
            node("proxy", node("<display>", node("false", "true"))),
            node("game", node("<display>", node("false", "true", node("false", "true"))))),
        node("delete", node("<display>")))

    override fun execute(args: Array<String>) {
        if (args.size >= 2) {
            if (args[0].equals("create", true)) {
                if ((args[1].equals("proxy", true) && args.size == 4 &&
                            (args[3].equals("false", true) || args[3].equals("true", true))) ||
                    (args[1].equals("game", true) && args.size == 5 &&
                            (args[3].equals("false", true) || args[3].equals("true", true)) &&
                            (args[4].equals("false", true) || args[4].equals("true", true)))
                ) {
                    try {
                        val groupType: GroupType = GroupType.valueOf(args[1].uppercase())
                        val displayName: String = args[2]
                        val lobby: Boolean = if (groupType == GroupType.GAME) args[3].toBoolean() else false
                        val static: Boolean = if (groupType == GroupType.PROXY) args[3].toBoolean() else if (groupType == GroupType.GAME) args[4].toBoolean() else false

                        if (NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.any { it.name.equals(displayName, true) }) {
                            NoobCloud.instance.logger.error("A group with this name already exists!")
                            return
                        }
                        if (NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.any { it.name.equals(displayName, true) }) {
                            NoobCloud.instance.logger.error("A group with this name already exists!")
                            return
                        }

                        if (groupType == GroupType.PROXY) {
                            NoobCloud.instance.cloudConfig.addProxyGroup(ProxyData(displayName, 256, 1, 1, -1, static))

                            NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.first { it.name == displayName }.also { proxyData ->
                                repeat(proxyData.minAmount) {
                                    NoobCloud.instance.processManager.startProxyServer(proxyData)
                                }
                            }
                        } else if (groupType == GroupType.GAME) {
                            NoobCloud.instance.cloudConfig.addGameGroup(GameData(displayName, 512, 1, 5, 10, lobby, static))

                            NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.first { it.name == displayName }.also { gameData ->
                                repeat(gameData.minAmount) {
                                    NoobCloud.instance.processManager.startGameServer(gameData)
                                }
                            }
                        }

                        NoobCloud.instance.logger.info("The group $displayName (${groupType.toString().lowercase().replaceFirstChar(Char::titlecase)}) was successfully created!")
                        NoobCloud.instance.logger.info("Edit the related config file to change values like RAM and min-amount. If you need help, read the documentation.")
                    } catch (exception: IllegalArgumentException) {
                        sendHelp()
                    }
                } else {
                    sendHelp()
                }
            } else if (args[0].equals("delete", true) && args.size == 2) {
                val displayName: String = args[1]

                val proxyData: ProxyData? = NoobCloud.instance.cloudConfig.proxyGroupsConfigData.proxies.find { it.name.equals(displayName, true) }
                if (proxyData != null) {
                    NoobCloud.instance.processManager.getProxies().filter { it.proxyData == proxyData }.forEach {
                        NoobCloud.instance.processManager.stopServer(it.name)
                    }
                    NoobCloud.instance.cloudConfig.removeProxyGroup(proxyData)

                    NoobCloud.instance.logger.info("The group " + proxyData.name + " was successfully deleted.")
                } else {
                    val gameData: GameData? = NoobCloud.instance.cloudConfig.gameGroupsConfigData.games.find { it.name.equals(displayName, true) }
                    if (gameData != null) {
                        NoobCloud.instance.processManager.getGames().filter { it.gameData == gameData }.forEach {
                            NoobCloud.instance.processManager.stopServer(it.name)
                        }
                        NoobCloud.instance.cloudConfig.removeGameGroup(gameData)

                        NoobCloud.instance.logger.info("The group " + gameData.name + " was successfully deleted.")
                    } else {
                        NoobCloud.instance.logger.error("There is no group with this name!")
                    }
                }
            } else {
                sendHelp()
            }
        } else {
            sendHelp()
        }
    }

    private fun sendHelp() {
        NoobCloud.instance.logger.info("Please use /group create proxy <display> <static>")
        NoobCloud.instance.logger.info("Please use /group create game <display> <lobby> <static>")
        NoobCloud.instance.logger.info("Please use /group delete <display>")
    }
}