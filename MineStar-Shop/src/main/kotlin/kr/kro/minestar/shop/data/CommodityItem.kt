package kr.kro.minestar.shop.data

import kr.kro.minestar.utility.item.amount
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class CommodityItem(private val shopFile: File, private val shopYaml: YamlConfiguration, page: Int, slot: Int) {

    private val key = "$page.$slot"

    var item = shopYaml.getItemStack("$key.item")?.amount(1)

    var currency = Cu

    private var buyPrice = if (shopYaml.isLong("$key.buy")) shopYaml.getLong("$key.buy")
    else null

    fun buyPrice() = buyPrice
    fun buyPrice(price: Long) {
        buyPrice = price
        shopYaml["$key.buy"] = price
        save()
    }


    private var sellPrice = if (shopYaml.isLong("$key.sell")) shopYaml.getLong("$key.sell")
    else null

    fun sellPrice() = sellPrice
    fun sellPrice(price: Long) {
        sellPrice = price
        shopYaml["$key.sell"] = price
        save()
    }


    fun save() {
        shopYaml.save(shopFile)
    }
}