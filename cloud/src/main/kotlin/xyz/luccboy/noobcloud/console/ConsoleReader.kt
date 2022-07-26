package xyz.luccboy.noobcloud.console

import xyz.luccboy.noobcloud.NoobCloud
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.terminal.Terminal
import org.jline.terminal.Terminal.SignalHandler
import org.jline.terminal.TerminalBuilder
import kotlin.system.exitProcess

class ConsoleReader {

    val terminal: Terminal = TerminalBuilder.builder()
        .nativeSignals(true)
        .signalHandler(SignalHandler.SIG_IGN)
        .build()

    fun start() {
        val lineReader: LineReader = LineReaderBuilder.builder().terminal(terminal).build()

        while (!NoobCloud.instance.stopping) {
            try {
                val input: String = lineReader.readLine("> ")
                if (input.trim().isNotEmpty()) {
                    val executed: Boolean = NoobCloud.instance.commandHandler.handleInput(input.split(" ").toTypedArray())
                    if (!executed) NoobCloud.instance.logger.error("Unknown command, please use \"help\"!")
                }
            } catch (exception: UserInterruptException) {
                NoobCloud.instance.logger.info("NoobCloud will be stopped.")
                NoobCloud.instance.forceStop()
                exitProcess(0)
            } catch (exception: Exception) {
                return
            }
        }
    }

}