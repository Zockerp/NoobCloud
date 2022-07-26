package xyz.luccboy.noobcloud.console.commands

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.console.Command

class HelpCommand : Command {
    override val name: String = "help"
    override val description: String = "Lists all commands"
    override val aliases: Array<String> = arrayOf("help", "?")

    override fun execute(args: Array<String>) {
        NoobCloud.instance.logger.info("Here is a list of all commands:")
        NoobCloud.instance.commandHandler.commandList.forEach { command ->
           NoobCloud.instance.logger.info(command.name + " - " + command.description)
        }
    }
}