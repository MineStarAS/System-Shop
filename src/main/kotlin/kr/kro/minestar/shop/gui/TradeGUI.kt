package kr.kro.minestar.shop.gui

import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.shop.Command.warningScript
import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.data.CommodityItem
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.inventory.howManyHasSameItem
import kr.kro.minestar.utility.inventory.howManyToAdd
import kr.kro.minestar.utility.item.*
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class TradeGUI(override val player: Player, private val shopGUI: ShopGUI, private val commodityItem: CommodityItem) : GUI() {
    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        BUY_1(1, 1, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f1개 구매§a]").amount(1)),
        BUY_5(1, 2, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f5개 구매§a]").amount(5)),
        BUY_16(1, 3, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f16개 구매§a]").amount(16)),
        BUY_32(1, 4, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f32개 구매§a]").amount(32)),
        BUY_64(1, 5, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f64개 구매§a]").amount(64)),
        BUY_ALL(1, 7, Main.head.item(9945, Material.LIME_CONCRETE).display("§a[§f최대 구매§a]")),

        SELL_1(2, 1, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f1개 판매§9]").amount(1)),
        SELL_5(2, 2, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f5개 판매§9]").amount(5)),
        SELL_16(2, 3, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f16개 판매§9]").amount(16)),
        SELL_32(2, 4, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f32개 판매§9]").amount(32)),
        SELL_64(2, 5, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f64개 판매§9]").amount(64)),
        SELL_ALL(2, 7, Main.head.item(8973, Material.BLUE_CONCRETE).display("§9[§f최대 판매§9]")),

        GO_TO_BACK_GUI(2, 8, Main.head.item(9982, Material.LIGHT_GRAY_CONCRETE).display("§7[§f뒤로 가기§7]")),
        ;
    }

    override val plugin = Main.plugin
    override val gui = InventoryUtil.gui(3, "아이템 거래")

    private val playerPurse = PlayerPurse.getPlayerPurse(player)!!

    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()

        setItems(Button.values())

        gui.setItem(4, commodityItem.showcaseItem())
        gui.setItem(9 + 8, playerPurseItem())
    }

    override fun setItems(slots: Array<out Slot>) {
        for (slot in slots) {
            if (slot !is Button) continue
            val item = slot.item.clone()
            item.clearLore()
            when (true) {
                slot.name.contains("BUY") -> {
                    val amount = if (slot.name.contains("ALL")) maxBuyAmount()
                    else slot.item.amount

                    item.addBuyLore(amount)
                }

                slot.name.contains("SELL") -> {
                    val amount = if (slot.name.contains("ALL")) player.inventory.howManyHasSameItem(commodityItem.item()!!)
                    else slot.item.amount
                    item.addSellLore(amount)
                }

                else -> {}
            }
            gui.setItem(slot.getIndex(), item)
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return
        val clickItem = e.currentItem ?: return

        when (getSlotByDisplay(clickItem, Button.values())) {
            Button.BUY_1 -> buy(1)
            Button.BUY_5 -> buy(5)
            Button.BUY_16 -> buy(16)
            Button.BUY_32 -> buy(32)
            Button.BUY_64 -> buy(64)
            Button.BUY_ALL -> buy(maxBuyAmount())

            Button.SELL_1 -> sell(1)
            Button.SELL_5 -> sell(5)
            Button.SELL_16 -> sell(16)
            Button.SELL_32 -> sell(32)
            Button.SELL_64 -> sell(64)
            Button.SELL_ALL -> sell(player.inventory.howManyHasSameItem(commodityItem.item()!!))

            Button.GO_TO_BACK_GUI -> shopGUI.openGUI()
        }
    }

    private fun buy(amount: Int) {
        if (amount < 0) return
        val item = commodityItem.item()!!.clone()

        val price = amount * commodityItem.buyPrice().price!!
        val currency = commodityItem.buyPrice().currency!!

        val inventory = player.inventory

        if (playerPurse.currencyAmount(currency) < price) return "§c보유 중인 금액이 부족합니다.".warningScript(player)
        if (inventory.howManyToAdd(item) < amount) return "§c인벤토리 공간이 부족합니다.".warningScript(player)
        inventory.addItem(item.amount(amount))
        displaying()

        val itemDisplay = item.display()

        "$itemDisplay §f을/를 §e$amount §f개 §a구매 §f하였습니다.".script(plugin.prefix).toPlayer(player)
        "§e[§f보유 금액§e] §f${playerPurse.currencyAmount(currency).addComma()} §6$currency".toPlayer(player)
    }

    private fun sell(amount: Int) {
        if (amount < 0) return
        val item = commodityItem.item()!!.clone()

        val price = amount * commodityItem.buyPrice().price!!
        val currency = commodityItem.buyPrice().currency!!

        val inventory = player.inventory

        if (inventory.howManyHasSameItem(item) < amount) return "§c보유 갯수가 부족합니다.".warningScript(player)
        inventory.remove(item.amount(amount))
        displaying()

        val itemDisplay = item.display()

        "$itemDisplay §f을/를 §e$amount §f개 §9판매 §f하였습니다.".script(plugin.prefix).toPlayer(player)
        "§e[§f보유 금액§e] §f${playerPurse.currencyAmount(currency).addComma()} §6$currency".toPlayer(player)
    }

    private fun maxBuyAmount(): Int {
        val currency = commodityItem.buyPrice().currency!!
        val howManyBuy = playerPurse.currencyAmount(currency) / commodityItem.buyPrice().price!!
        return min(player.inventory.howManyToAdd(commodityItem.item()!!), howManyBuy.toInt())
    }

    /**
     * Item function
     */
    private fun ItemStack.addBuyLore(amount: Int) {
        val item = this

        item.addLore(" ")

        if (!commodityItem.buyPrice().isValid()) {
            item.type = Material.AIR
            return
        }

        val price = commodityItem.buyPrice().price!!
        val currency = commodityItem.buyPrice().currency!!

        item.addLore("§7총 ${(price * amount).addComma()} $currency")
    }

    private fun ItemStack.addSellLore(amount: Int) {
        val item = this

        item.addLore(" ")

        if (!commodityItem.sellPrice().isValid()) {
            item.type = Material.AIR
            return
        }

        val price = commodityItem.sellPrice().price!!
        val currency = commodityItem.sellPrice().currency!!

        item.addLore("§7총 ${(price * amount).addComma()} $currency")
    }

    private fun playerPurseItem(): ItemStack {
        val item = Main.head.item(player)
        val buyCurrency = commodityItem.buyPrice().currency
        val sellCurrency = commodityItem.sellPrice().currency
        item.display("§e${player.name} §6소지금")
        if (buyCurrency == null && sellCurrency == null) return item
        if (buyCurrency == sellCurrency) {
            item.addLore(" ")
            item.addLore("${playerPurse.currencyAmount(buyCurrency!!)} §6$buyCurrency")
        } else {
            if (buyCurrency != null) {
                item.addLore(" ")
                item.addLore("${playerPurse.currencyAmount(buyCurrency)} §6$buyCurrency")
            }
            if (sellCurrency != null) {
                item.addLore(" ")
                item.addLore("${playerPurse.currencyAmount(sellCurrency)} §6$sellCurrency")
            }
        }

        return item
    }
}