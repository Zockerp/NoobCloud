package xyz.luccboy.noobcloud.api.server

/**
 * Represents all different states of a server
 */
enum class GameState {
    /**
     * Indicates that the server is a proxy
     */
    PROXY,

    /**
     * Indicates that the server is available and players can join
     */
    AVAILABLE,

    /**
     * Indicates that the countdown (of a minigame) has started
     */
    COUNTDOWN,

    /**
     * Indicates that the minigame has started
     */
    INGAME,

    /**
     * Indicates that the minigame has ended
     */
    ENDING
}