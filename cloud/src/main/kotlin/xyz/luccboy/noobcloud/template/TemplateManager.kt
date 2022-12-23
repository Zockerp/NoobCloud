package xyz.luccboy.noobcloud.template

import xyz.luccboy.noobcloud.NoobCloud
import xyz.luccboy.noobcloud.config.GameData
import xyz.luccboy.noobcloud.config.ProxyData
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class TemplateManager {

    private val mapper: ObjectMapper = TomlMapper().registerKotlinModule()

    fun createFiles() {
        File("templates").also { if (!it.exists()) it.mkdirs() }
        File("templates/proxy").also { if (!it.exists()) it.mkdirs() }
        File("templates/game").also { if (!it.exists()) it.mkdirs() }
        File("libraries").also { if (!it.exists()) it.mkdirs() }
    }

    fun createTemplateDirectory(name: String, groupType: GroupType) {
        val templateDirectory = File("${groupType.templatePath}/$name")
        if (!templateDirectory.exists()) templateDirectory.mkdirs()
        if (groupType == GroupType.PROXY) {
            File(templateDirectory.path + "/plugins").also { if (!it.exists()) it.mkdirs() }
        } else {
            File(templateDirectory.path + "/extensions").also { if (!it.exists()) it.mkdirs() }
        }
    }

    fun deleteTemplateDirectory(name: String, groupType: GroupType) {
        val templateDirectory = File("${groupType.templatePath}/$name")
        if (templateDirectory.exists()) templateDirectory.deleteRecursively()
    }

    fun copyProxyTemplate(id: Int, port: Int, proxyData: ProxyData): Boolean {
        val templateDirectory = File("${GroupType.PROXY.templatePath}/${proxyData.name}")
        val workDirectory = File((if (!proxyData.static) GroupType.PROXY.tempPath else GroupType.PROXY.staticPath) + "/${proxyData.name}/${proxyData.name}-$id").also {
            if (it.exists()) it.deleteRecursively()
            it.mkdirs()
        }

        if (!File("libraries/velocity.jar").exists()) {
            NoobCloud.instance.logger.error("No velocity.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/velocity.jar").copyTo(File("${workDirectory.path}/velocity.jar"), true)

        templateDirectory.copyRecursively(workDirectory, true)

        val configFile = File("${workDirectory.path}/velocity.toml")
        if (!configFile.exists()) NoobCloud.instance.cloudConfig.exportResource("velocity.toml", workDirectory.path, false)
        val entries: MutableMap<String, Any> = mapper.readValue(configFile, object : TypeReference<MutableMap<String, Any>>() {})
        entries["bind"] = NoobCloud.instance.cloudConfig.noobCloudConfigData.config.address + ":$port"
        mapper.writeValue(configFile, entries)

        if (!File("libraries/NoobCloudPlugin.jar").exists()) {
            NoobCloud.instance.logger.error("No NoobCloudPlugin.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/NoobCloudPlugin.jar").copyTo(File("${workDirectory.path}/plugins/NoobCloudPlugin.jar"), true)

        if (!File("configs/messages.yml").exists()) {
            NoobCloud.instance.logger.error("No messages.yml found! Please restart NoobCloud!")
            return false
        }
        File("configs/messages.yml").copyTo(File("${workDirectory.path}/plugins/NoobCloud/messages.yml"), true)

        return true
    }

    fun copyGameTemplate(id: Int, gameData: GameData): Boolean {
        val templateDirectory = File("${GroupType.GAME.templatePath}/${gameData.name}")
        val workDirectory = File((if (!gameData.static) GroupType.GAME.tempPath else GroupType.GAME.staticPath) + "/${gameData.name}/${gameData.name}-$id").also {
            if (it.exists()) it.deleteRecursively()
            it.mkdirs()
        }

        if (!File("libraries/minestom.jar").exists()) {
            NoobCloud.instance.logger.error("No minestom.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/minestom.jar").copyTo(File("${workDirectory.path}/minestom.jar"), true)

        templateDirectory.copyRecursively(workDirectory, true)

        if (!File("libraries/NoobCloudPlugin.jar").exists()) {
            NoobCloud.instance.logger.error("No NoobCloudPlugin.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/NoobCloudPlugin.jar").copyTo(File("${workDirectory.path}/extensions/NoobCloudPlugin.jar"), true)

        if (!File("configs/messages.yml").exists()) {
            NoobCloud.instance.logger.error("No messages.yml found! Please restart NoobCloud!")
            return false
        }
        File("configs/messages.yml").copyTo(File("${workDirectory.path}/extensions/NoobCloud/messages.yml"), true)

        return true
    }

    fun saveServerTemplate(groupName: String, groupType: GroupType, serverName: String, static: Boolean) {
        val templateDirectory = File("${groupType.templatePath}/$groupName")
        val workDirectory = File((if (!static) groupType.tempPath else groupType.staticPath) + "/${groupName}/${serverName}")
        workDirectory.copyRecursively(templateDirectory, true)
    }

}