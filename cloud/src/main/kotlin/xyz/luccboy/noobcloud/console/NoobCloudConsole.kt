package xyz.luccboy.noobcloud.console

import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.jline.builtins.Completers.TreeCompleter
import org.jline.builtins.Completers.TreeCompleter.Node
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import xyz.luccboy.noobcloud.NoobCloud

class NoobCloudConsole : SimpleTerminalConsole() {

    override fun buildReader(builder: LineReaderBuilder): LineReader {
        val commands: List<Node> = NoobCloud.instance.commandHandler.commandList.map { it.completer }

        return super.buildReader(builder
            .appName("NoobCloud")
            .completer(TreeCompleter(commands))
        )
    }

    override fun isRunning(): Boolean = NoobCloud.instance.running

    override fun runCommand(command: String) {
        if (!NoobCloud.instance.commandHandler.handleInput(command.split(" ").toTypedArray())) {
            NoobCloud.instance.logger.error("Unknown command, use \"help\" for help!")
        }
    }

    override fun shutdown() {
        NoobCloud.instance.logger.info("NoobCloud will be stopped.")
        NoobCloud.instance.stop()
    }
}