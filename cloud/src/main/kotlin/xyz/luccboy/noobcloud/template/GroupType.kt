package xyz.luccboy.noobcloud.template

enum class GroupType(val templatePath: String, val tempPath: String) {
    PROXY("templates/proxy", "temp/proxy"),
    GAME("templates/game", "temp/game")
}