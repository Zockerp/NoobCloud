package xyz.luccboy.noobcloud.console

import xyz.luccboy.noobcloud.console.commands.*

class CommandHandler {

    val commandList: MutableList<Command> = mutableListOf()

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

    fun registerCommands() {
        commandList.add(HelpCommand())
        commandList.add(ClearCommand())
        commandList.add(StopCommand())
        commandList.add(GroupCommand())
        commandList.add(ServerCommand())
        commandList.add(ScreenCommand())
        commandList.add(RestartCommand())
    }

}