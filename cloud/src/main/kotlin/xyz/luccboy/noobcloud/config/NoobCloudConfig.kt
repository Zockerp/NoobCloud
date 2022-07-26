package xyz.luccboy.noobcloud.config

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.template.GroupType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import kotlin.io.path.Path

class NoobCloudConfig {

    lateinit var noobCloudConfigData: NoobCloudConfigData
        private set
    lateinit var proxyGroupsConfigData: ProxyGroupConfigData
        private set
    lateinit var gameGroupsConfigData: GameGroupConfigData
        private set

    fun createConfigFiles(): NoobCloudConfig {
        exportResource("config.yml", "configs")
        exportResource("proxy-groups.yml", "configs")
        exportResource("game-groups.yml", "configs")
        return this
    }

    private val mapper: ObjectMapper = YAMLMapper().registerKotlinModule()

    fun loadConfigs(): NoobCloudConfig {
        try {
            noobCloudConfigData = Files.newBufferedReader(Path("configs/config.yml")).use { mapper.readValue(it, NoobCloudConfigData::class.java) }
            proxyGroupsConfigData = Files.newBufferedReader(Path("configs/proxy-groups.yml")).use { mapper.readValue(it, ProxyGroupConfigData::class.java) }
            gameGroupsConfigData = Files.newBufferedReader(Path("configs/game-groups.yml")).use { mapper.readValue(it, GameGroupConfigData::class.java) }

            proxyGroupsConfigData.proxies.forEach { proxyData -> NoobCloud.instance.templateManager.createTemplateDirectory(proxyData.name, GroupType.PROXY) }
            gameGroupsConfigData.games.forEach { gameData -> NoobCloud.instance.templateManager.createTemplateDirectory(gameData.name, GroupType.GAME) }
        } catch (exception: Exception) {
            NoobCloud.instance.logger.error(exception)
        }

        return this
    }

    fun addProxyGroup(proxyData: ProxyData) {
        proxyGroupsConfigData.proxies.add(proxyData)
        mapper.writeValue(File("configs/proxy-groups.yml"), proxyGroupsConfigData)
        NoobCloud.instance.templateManager.createTemplateDirectory(proxyData.name, GroupType.PROXY)
    }

    fun removeProxyGroup(proxyData: ProxyData) {
        proxyGroupsConfigData.proxies.remove(proxyData)
        mapper.writeValue(File("configs/proxy-groups.yml"), proxyGroupsConfigData)
        NoobCloud.instance.templateManager.deleteTemplateDirectory(proxyData.name, GroupType.PROXY)
    }

    fun addGameGroup(gameData: GameData) {
        gameGroupsConfigData.games.add(gameData)
        mapper.writeValue(File("configs/game-groups.yml"), gameGroupsConfigData)
        NoobCloud.instance.templateManager.createTemplateDirectory(gameData.name, GroupType.GAME)
    }

    fun removeGameGroup(gameData: GameData) {
        gameGroupsConfigData.games.remove(gameData)
        mapper.writeValue(File("configs/game-groups.yml"), gameGroupsConfigData)
        NoobCloud.instance.templateManager.deleteTemplateDirectory(gameData.name, GroupType.GAME)
    }

    fun exportResource(resourceName: String, path: String, message: Boolean = true) {
        val file = File("$path/$resourceName")
        if (!file.exists()) {
            val inputStream: InputStream? = this.javaClass.classLoader.getResourceAsStream(resourceName)
            val parentFile: File = file.parentFile
            if (!parentFile.exists()) parentFile.mkdirs()
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath())
            } else {
                file.createNewFile()
            }

            if (message) NoobCloud.instance.logger.info("The file ${file.name} was created.")
        }
    }

}