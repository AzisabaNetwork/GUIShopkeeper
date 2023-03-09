package net.azisaba.guishopkeeper.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import java.io.File

object Config {
    /**
     * Configuration file
     */
    private val file = File("./plugins/GUIShopkeeper/config.yml")

    /**
     * The configuration
     */
    @JvmStatic
    val config = file.let {
        if (!it.exists()) {
            it.parentFile.mkdirs()
            it.writeText(Yaml.default.encodeToString(GUIShopkeeperConfig()))
        }
        Yaml.default.decodeFromString(GUIShopkeeperConfig.serializer(), it.readText())
    }

    /**
     * Save config
     */
    fun save() {
        try {
            file.writeText(Yaml.default.encodeToString(config))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        save()
    }
}
