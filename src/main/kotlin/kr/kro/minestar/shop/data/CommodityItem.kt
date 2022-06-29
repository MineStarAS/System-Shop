package kr.kro.minestar.shop.data

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.string.toServer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

class CommodityItem(private val shopFile: File, private val shopYaml: YamlConfiguration, page: Int, slot: Int) {

    private val key = "$page.$slot"

    /**
     * Item function
     */
    private var item = shopYaml.getItemStack("$key.item")?.amount(1)

    fun item() = item?.clone()?.amount(1)

    fun item(item: ItemStack?) {
        this.item = item?.clone()?.amount(1)
        shopYaml["$key.item"] = this.item
        save()
    }

    fun showcaseItem(): ItemStack? {
        val item = item?.clone() ?: return null

        if (buyPrice.isValid())
            if (buyPrice.price!! >= 0)
                item.addLore(" ").addLore("§a[§f구매 가격§a] $buyPrice")

        if (sellPrice.isValid())
            if (sellPrice.price!! >= 0)
                item.addLore(" ").addLore("§9[§f판매 가격§9] $sellPrice")

        return item
    }

    fun editItem(): ItemStack? {
        val item = item?.clone() ?: return null

                item.addLore(" ").addLore("§a[§f구매 가격§a] $buyPrice")

                item.addLore(" ").addLore("§9[§f판매 가격§9] $sellPrice")

        return item
    }

    /**
     * Buy price function
     */
    private var buyPrice = loadBuyPrice()

    fun buyPrice() = buyPrice

    fun buyPrice(currency: Currency?) {
        val price = buyPrice.price
        buyPrice = CurrencyPrice(currency, price)
        shopYaml["$key.buy.currency"] = currency.toString()
        save()
    }

    fun buyPrice(price: Long?) {
        val currency = buyPrice.currency
        buyPrice = CurrencyPrice(currency, price)
        shopYaml["$key.buy.price"] = price
        save()
    }

    private fun loadBuyPrice(): CurrencyPrice {
        val currency = Currency.getCurrency(shopYaml.getString("$key.buy.currency"))
        val price = if (shopYaml.isLong("$key.buy.price") || shopYaml.isInt("$key.buy.price")) shopYaml.getLong("$key.buy.price")
        else null
        return CurrencyPrice(currency, price)
    }

    /**
     * Sell price function
     */
    private var sellPrice = loadSellPrice()

    fun sellPrice() = sellPrice

    fun sellPrice(currency: Currency?) {
        val price = sellPrice.price
        sellPrice = CurrencyPrice(currency, price)
        shopYaml["$key.sell.currency"] = currency.toString()
        save()
    }

    fun sellPrice(price: Long?) {
        val currency = sellPrice.currency
        sellPrice = CurrencyPrice(currency, price)
        shopYaml["$key.sell.price"] = price
        save()
    }

    private fun loadSellPrice(): CurrencyPrice {
        val currency = Currency.getCurrency(shopYaml.getString("$key.sell.currency"))
        val price = if (shopYaml.isLong("$key.sell.price") || shopYaml.isInt("$key.sell.price")) shopYaml.getLong("$key.sell.price")
        else null
        return CurrencyPrice(currency, price)
    }

    private fun save() {
        shopYaml.save(shopFile)
    }
}