package xyz.luccboy.noobcloud.console

import org.jline.builtins.Completers.TreeCompleter.Node

interface Command {
    val name: String
    val description: String
    val aliases: Array<String>
    val completer: Node
    fun execute(args: Array<String>)
}