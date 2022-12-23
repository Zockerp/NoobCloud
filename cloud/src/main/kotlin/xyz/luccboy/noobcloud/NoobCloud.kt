package xyz.luccboy.noobcloud

import xyz.luccboy.noobcloud.config.NoobCloudConfig
import xyz.luccboy.noobcloud.console.CommandHandler
import xyz.luccboy.noobcloud.network.NettyServer
import xyz.luccboy.noobcloud.server.ProcessManager
import xyz.luccboy.noobcloud.template.TemplateManager
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.ParameterizedMessageFactory
import xyz.luccboy.noobcloud.console.NoobCloudConsole
import java.io.File
import kotlin.system.exitProcess

class NoobCloud {

    companion object {
        lateinit var instance: NoobCloud
            private set
    }

    init {
        instance = this
    }

    var running: Boolean = true

    val logger: Logger = LogManager.getLogger(NoobCloud::class.java.simpleName, ParameterizedMessageFactory())
    val commandHandler: CommandHandler = CommandHandler()
    val cloudConfig: NoobCloudConfig = NoobCloudConfig()
    val templateManager: TemplateManager = TemplateManager()
    lateinit var processManager: ProcessManager
        private set
    val console: NoobCloudConsole = NoobCloudConsole()
    lateinit var nettyServer: NettyServer

    suspend fun launch() = coroutineScope {
        logger.info("\n" +
                " __    _  _______  _______  _______  _______  ___      _______  __   __  ______  \n" +
                "|  |  | ||       ||       ||  _    ||       ||   |    |       ||  | |  ||      | \n" +
                "|   |_| ||   _   ||   _   || |_|   ||       ||   |    |   _   ||  | |  ||  _    |\n" +
                "|       ||  | |  ||  | |  ||       ||       ||   |    |  | |  ||  |_|  || | |   |\n" +
                "|  _    ||  |_|  ||  |_|  ||  _   | |      _||   |___ |  |_|  ||       || |_|   |\n" +
                "| | |   ||       ||       || |_|   ||     |_ |       ||       ||       ||       |\n" +
                "|_|  |__||_______||_______||_______||_______||_______||_______||_______||______| \n" +
                "by Luccboy\n"
        )

        File("temp").also { if (it.exists()) it.deleteRecursively() }
        cloudConfig.createConfigFiles().loadConfigs()
        templateManager.createFiles()
        processManager = ProcessManager().also { it.startDefaultServers() }

        nettyServer = NettyServer()

        commandHandler.registerCommands()
        launch(Dispatchers.IO) { console.start() }
    }

    fun stop() {
        running = false
        nettyServer.shutdown()
        processManager.servers.values.forEach { serverProcess -> serverProcess.process.destroyForcibly() }
        File("temp").also { if (it.exists()) it.deleteRecursively() }
        exitProcess(0)
    }

}