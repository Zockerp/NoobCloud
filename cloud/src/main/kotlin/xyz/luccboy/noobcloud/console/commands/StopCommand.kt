package xyz.luccboy.noobcloud.console.commands

import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command

class StopCommand : Command {
    override val name: String = "stop"
    override val description: String = "Stops the cloud and all servers"
    override val aliases: Array<String> = arrayOf("stop", "end", "shutdown")
    override val completer: Node = node(name, *aliases)

    override fun execute(args: Array<String>) {
        NoobCloud.instance.logger.info("NoobCloud will be stopped.")
        NoobCloud.instance.stop()
    }
}