package xyz.luccboy.noobcloud.template

enum class GroupType(val templatePath: String, val tempPath: String, val staticPath: String) {
    PROXY("templates/proxy", "temp/proxy", "static/proxy"),
    GAME("templates/game", "temp/game", "static/game")
}