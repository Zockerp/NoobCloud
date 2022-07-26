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
        NoobCloud.instance.logger.info("Waiting for all servers to be stopped...")
        NoobCloud.instance.logger.info("Press CTRL+C to force stop NoobCloud, this can cause errors.")
        NoobCloud.instance.stopping = true
        NoobCloud.instance.checkShutdown()

        NoobCloud.instance.processManager.servers.values.toList().forEach {
            NoobCloud.instance.nettyServer.channelsByUUID[it.uuid]?.writeAndFlush(ServerStopPacket(it.name, it.uuid))
        }
    }
}