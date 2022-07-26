package xyz.luccboy.noobcloud.console

import xyz.luccboy.noobcloud.console.commands.ClearCommand
import xyz.luccboy.noobcloud.console.commands.HelpCommand

class CommandHandler {

    val commandList: MutableList<Command> = mutableListOf()

    fun registerCommand(command: Command) = commandList.add(command)

    fun handleInput(input: Array<String>): Boolean {
        val commandName: String = input[0]
        val args: Array<String> = input.drop(1).toTypedArray()
        commandList.forEach { command ->
            if (command.aliases.contains(commandName.lowercase())) {
                command.execute(args)
                return true
            }
        }
        return false
    }

    init {
        commandList.add(HelpCommand())
        commandList.add(ClearCommand())
    }

}