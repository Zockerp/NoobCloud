package xyz.luccboy.noobcloud.console.commands

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command
import org.jline.utils.InfoCmp.Capability

class ClearCommand : Command {
    override val name: String = "clear"
    override val description: String = "Clears the console"
    override val aliases: Array<String> = arrayOf("clear")

    override fun execute(args: Array<String>) {
        NoobCloud.instance.consoleReader.terminal.puts(Capability.clear_screen)
        NoobCloud.instance.consoleReader.terminal.flush()
    }
}