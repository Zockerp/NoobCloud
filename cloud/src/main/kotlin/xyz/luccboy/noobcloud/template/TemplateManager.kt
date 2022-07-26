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
        val tempDirectory = File(GroupType.PROXY.tempPath + "/${proxyData.name}/${proxyData.name}-$id").also {
            if (it.exists()) it.deleteRecursively()
            it.mkdirs()
        }

        if (!File("libraries/velocity.jar").exists()) {
            NoobCloud.instance.logger.error("No velocity.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/velocity.jar").copyTo(File("${tempDirectory.path}/velocity.jar"), true)

        templateDirectory.copyRecursively(tempDirectory, true)

        val configFile = File("${tempDirectory.path}/velocity.toml")
        if (!configFile.exists()) NoobCloud.instance.cloudConfig.exportResource("velocity.toml", tempDirectory.path, false)
        val entries: MutableMap<String, Any> = mapper.readValue(configFile, object : TypeReference<MutableMap<String, Any>>() {})
        entries["bind"] = NoobCloud.instance.cloudConfig.noobCloudConfigData.config.address + ":$port"
        mapper.writeValue(configFile, entries)

        if (!File("libraries/NoobCloudPlugin.jar").exists()) {
            NoobCloud.instance.logger.error("No NoobCloudPlugin.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/NoobCloudPlugin.jar").copyTo(File("${tempDirectory.path}/plugins/NoobCloudPlugin.jar"), true)

        return true
    }

    fun copyGameTemplate(id: Int, gameData: GameData): Boolean {
        val templateDirectory = File("${GroupType.GAME.templatePath}/${gameData.name}")
        val tempDirectory = File(GroupType.GAME.tempPath + "/${gameData.name}/${gameData.name}-$id").also {
            if (it.exists()) it.deleteRecursively()
            it.mkdirs()
        }

        if (!File("libraries/minestom.jar").exists()) {
            NoobCloud.instance.logger.error("No minestom.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/minestom.jar").copyTo(File("${tempDirectory.path}/minestom.jar"), true)

        templateDirectory.copyRecursively(tempDirectory, true)

        if (!File("libraries/NoobCloudPlugin.jar").exists()) {
            NoobCloud.instance.logger.error("No NoobCloudPlugin.jar found! Please add it in the libraries folder!")
            return false
        }
        File("libraries/NoobCloudPlugin.jar").copyTo(File("${tempDirectory.path}/extensions/NoobCloudPlugin.jar"), true)

        return true
    }

    fun saveServerTemplate(groupName: String, groupType: GroupType, serverName: String) {
        val templateDirectory = File("${groupType.templatePath}/$groupName")
        val tempDirectory = File(groupType.tempPath + "/${groupName}/${serverName}")
        tempDirectory.copyRecursively(templateDirectory, true)
    }

}