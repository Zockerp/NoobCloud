package xyz.luccboy.noobcloud.console.commands

import net.minecrell.terminalconsole.TerminalConsoleAppender
import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.builtins.Completers.TreeCompleter.node
import org.jline.utils.InfoCmp.Capability
import xyz.luccboy.noobcloud.console.Command

class ClearCommand : Command {
    override val name: String = "clear"
    override val description: String = "Clears the console"
    override val aliases: Array<String> = arrayOf("clear")
    override val completer: Node = node(name, *aliases)

    override fun execute(args: Array<String>) {
        TerminalConsoleAppender.getTerminal()?.let {
            it.puts(Capability.clear_screen)
            it.flush()
        }
    }
}