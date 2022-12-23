package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command
import xyz.luccboy.noobcloud.server.ServerProcess
import java.io.BufferedReader
import java.io.InputStreamReader

class ScreenCommand : Command {
    override val name: String = "screen"
    override val description: String = "Shows the console-output of a server"
    override val aliases: Array<String> = arrayOf("screen")
    override val completer: Node = node(name, *aliases, node("<display-id>", "leave"))

    private var outputServer: String = ""
    private var outputThread: Thread? = null

    override fun execute(args: Array<String>) {
        if (args.size == 1) {
            try {
                if (outputServer == args[0] || args[0].equals("leave", true)) {
                    outputThread?.stop()
                    outputServer = ""
                    if (!args[0].equals("leave", true)) {
                        NoobCloud.instance.logger.info("The console-output of ${args[0]} was stopped.")
                    } else {
                        NoobCloud.instance.logger.info("The console-output was stopped.")
                    }
                    return
                }

                if (!NoobCloud.instance.processManager.servers.values.any { it.name == args[0] }) {
                    NoobCloud.instance.logger.error("There is no server with this name!")
                    return
                }

                NoobCloud.instance.logger.info("You now receive the console-output of ${args[0]}.")

                val serverProcess: ServerProcess = NoobCloud.instance.processManager.getServerProcess(args[0])
                if (outputThread != null && outputThread!!.isAlive) outputThread?.stop()
                outputServer = args[0]
                outputThread = Thread {
                    while (NoobCloud.instance.running && serverProcess.process.isAlive) {
                        val input = BufferedReader(InputStreamReader(serverProcess.process.inputStream))
                        var line: String? = input.readLine()
                        while (line != null) {
                            NoobCloud.instance.logger.info("[${args[0]}] $line")
                            line = input.readLine()
                        }
                    }
                }
                outputThread?.start()
            } catch (indexOutOfBoundsException: IndexOutOfBoundsException) {
                NoobCloud.instance.logger.error("This server does not exist!")
            } catch (numberFormatException: NumberFormatException) {
                NoobCloud.instance.logger.error("This server does not exist!")
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        } else {
            sendHelp()
        }
    }

    private fun sendHelp() {
        NoobCloud.instance.logger.info("Please use /screen <display-id/leave>")
    }
}