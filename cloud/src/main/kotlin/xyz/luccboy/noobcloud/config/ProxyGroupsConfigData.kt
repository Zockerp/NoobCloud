package xyz.luccboy.noobcloud.config

data class ProxyData(val name: String, val memory: Int, val minAmount: Int, val maxAmount: Int, val startPlayerCount: Int, val static: Boolean)

data class ProxyGroupConfigData(val proxies: MutableList<ProxyData>)