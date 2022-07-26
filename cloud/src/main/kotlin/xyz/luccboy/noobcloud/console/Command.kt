package xyz.luccboy.noobcloud.console

interface Command {
    val name: String
    val description: String
    val aliases: Array<String>
    fun execute(args: Array<String>)
}