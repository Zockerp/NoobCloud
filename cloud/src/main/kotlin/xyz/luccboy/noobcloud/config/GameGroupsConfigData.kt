package xyz.luccboy.noobcloud.config

data class GameData(val name: String, val memory: Int, val minAmount: Int, val maxAmount: Int, val startPlayerCount: Int, val lobby: Boolean)

data class GameGroupConfigData(val games: MutableList<GameData>)