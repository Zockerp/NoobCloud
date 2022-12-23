package xyz.luccboy.noobcloud.plugin.shared.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import xyz.luccboy.noobcloud.api.group.GroupType
import java.nio.file.Files
import kotlin.io.path.Path

class Config(private val groupType: GroupType) {

    lateinit var messages: Messages
        private set

    private val mapper: ObjectMapper = YAMLMapper().registerKotlinModule()

    fun loadConfig() {
        try {
            val messagePath: String = (if (groupType == GroupType.PROXY) "plugins" else "extensions") + "/NoobCloud/messages.yml"
            messages = Files.newBufferedReader(Path(messagePath)).use { mapper.readValue(it, Messages::class.java) }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}