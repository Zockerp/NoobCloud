package xyz.luccboy.noobcloud.console.commands

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command

class StopCommand : Command {
    override val name: String = "stop"
    override val description: String = "Stops the cloud and all servers"
    override val aliases: Array<String> = arrayOf("stop", "end", "shutdown")

    override fun execute(args: Array<String>) {
        NoobCloud.instance.logger.info("NoobCloud will be stopped.")
        NoobCloud.instance.stop()
    }
}