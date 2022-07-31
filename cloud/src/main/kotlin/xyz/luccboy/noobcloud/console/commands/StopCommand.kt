package xyz.luccboy.noobcloud.console.commands

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command
import xyz.luccboy.noobcloud.library.network.packets.server.ServerStopPacket
import java.io.File
import kotlin.system.exitProcess

class StopCommand : Command {
    override val name: String = "stop"
    override val description: String = "Stops the cloud and all servers"
    override val aliases: Array<String> = arrayOf("stop", "end", "shutdown")

    override fun execute(args: Array<String>) {
        NoobCloud.instance.logger.info("NoobCloud will be stopped.")
        NoobCloud.instance.stop()
    }
}