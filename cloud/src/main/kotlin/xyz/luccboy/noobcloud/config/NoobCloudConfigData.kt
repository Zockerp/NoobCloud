package xyz.luccboy.noobcloud.config

data class CloudConfigData(val address: String, val port: Int, val proxyStartPort: Int, val gameStartPort: Int, val javaPath: String)
data class DatabaseDate(val enabled: Boolean, val host: String, val port: Int, val database: String, val username: String, val password: String)

data class NoobCloudConfigData(val config: CloudConfigData, val playerDatabase: DatabaseDate)