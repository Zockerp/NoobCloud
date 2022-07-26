package xyz.luccboy.noobcloud

import xyz.luccboy.noobcloud.config.NoobCloudConfig
import xyz.luccboy.noobcloud.console.CommandHandler
import xyz.luccboy.noobcloud.console.ConsoleReader
import xyz.luccboy.noobcloud.console.commands.*
import xyz.luccboy.noobcloud.network.NettyServer
import xyz.luccboy.noobcloud.server.ProcessManager
import xyz.luccboy.noobcloud.template.TemplateManager
import kotlinx.coroutines.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.message.ParameterizedMessageFactory
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

    val prefix: String = "§bNoobCloud §8| §7"
    var stopping: Boolean = false

    val logger: Logger = LogManager.getLogger(NoobCloud::class.java.simpleName, ParameterizedMessageFactory())
    val commandHandler: CommandHandler = CommandHandler()
    val cloudConfig: NoobCloudConfig = NoobCloudConfig()
    val templateManager: TemplateManager = TemplateManager()
    lateinit var processManager: ProcessManager
        private set
    val consoleReader: ConsoleReader = ConsoleReader()
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
                "by Luccboy"
        )

        File("temp").also { if (it.exists()) it.deleteRecursively() }
        cloudConfig.createConfigFiles().loadConfigs()
        templateManager.createFiles()
        processManager = ProcessManager().also { it.startDefaultServers() }

        nettyServer = NettyServer()

        registerCommands()
        Runtime.getRuntime().addShutdownHook(Thread { forceStop() })
        launch(Dispatchers.IO) { consoleReader.start() }
    }

    private fun registerCommands() {
        commandHandler.registerCommand(StopCommand())
        commandHandler.registerCommand(GroupCommand())
        commandHandler.registerCommand(ServerCommand())
        commandHandler.registerCommand(ScreenCommand())
        commandHandler.registerCommand(RestartCommand())
    }

    fun forceStop() {
        stopping = true
        processManager.servers.values.forEach { serverProcess -> serverProcess.process.destroyForcibly() }
    }

    fun checkShutdown() {
        if (stopping && processManager.servers.isEmpty()) {
            File("temp").also { if (it.exists()) it.deleteRecursively() }
            nettyServer.shutdown()
            exitProcess(0)
        }
    }

}