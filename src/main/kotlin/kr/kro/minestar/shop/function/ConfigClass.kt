package kr.kro.minestar.shop.function

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.shop.Main.Companion.plugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


object ConfigClass {
    private fun file() = File(plugin.dataFolder, "config.yml").apply {
        if (!exists()) plugin.saveResource("config.yml", false)
    }

    private fun config() = YamlConfiguration.loadConfiguration(file())

    fun simplePermission() = config().getBoolean("simplePermission")

    fun dataSaveFolder() = when (config().getString("dataSaveFolder")) {
        null,
        "null",
        "default",
        -> plugin.dataFolder
        else -> File(config().getString("dataSaveFolder")!!, "shop")
    }

    private fun mainCurrencyUnit() = config().getString("mainCurrency")
    fun mainCurrency() = Currency.getCurrency(mainCurrencyUnit())
}