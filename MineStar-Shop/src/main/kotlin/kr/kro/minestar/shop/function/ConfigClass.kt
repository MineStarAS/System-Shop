package kr.kro.minestar.shop.function

import kr.kro.minestar.shop.Main.Companion.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class ConfigClass {
    private val file = File(plugin.dataFolder, "config.yml").apply {
        if (!exists()) plugin.saveResource("config.yml", false)
    }

    private val config = YamlConfiguration.loadConfiguration(file)

    val simplePermission = config.getBoolean("simplePermission")

    val dataSaveFolder = when (config.getString("dataSaveFolder")) {
        null,
        "null",
        "default",
        -> plugin.dataFolder
        else -> File(config.getString("dataSaveFolder")!!, "shop")
    }
}