package kr.kro.minestar.shop.functions.gui

import kr.kro.minestar.money.data.DataClass
import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.Main.Companion.prefix
import kr.kro.minestar.shop.functions.ItemClass
import kr.kro.minestar.shop.functions.ItemClass.head
import kr.kro.minestar.shop.gui.Shop
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.hasSameItem
import kr.kro.minestar.utility.inventory.howManyHasSameItem
import kr.kro.minestar.utility.inventory.howManyToAdd
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack

class TradeGUI(override val player: Player, val shop: Shop, val item: ItemStack, val buyPrice: Int?, val sellPrice: Int?) : GUI() {
    override val pl = Main.pl

    val moneyData = DataClass.moneyData(player)!!
    val itemDisplay = if (item.itemMeta.hasDisplayName()) item.itemMeta.displayName
    else "§b${item.type}"
    val buyIcon = head(9944)
    val sellIcon = head(8955)
    val backIcon = head(9334).display("§c[§f뒤로 가기§c]")

    val buySlots = listOf(
        Slot(1, 1, buyIcon.clone().amount(1)),
        Slot(1, 2, buyIcon.clone().amount(5)),
        Slot(1, 3, buyIcon.clone().amount(16)),
        Slot(1, 4, buyIcon.clone().amount(32)),
        Slot(1, 5, buyIcon.clone().amount(64)),
    )
    val sellSlots = listOf(
        Slot(2, 1, sellIcon.clone().amount(1)),
        Slot(2, 2, sellIcon.clone().amount(5)),
        Slot(2, 3, sellIcon.clone().amount(16)),
        Slot(2, 4, sellIcon.clone().amount(32)),
        Slot(2, 5, sellIcon.clone().amount(64)),
    )

    override val gui = Bukkit.createInventory(null, 9 * 3, "[아이템 거래]")

    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()
        gui.setItem(4, item)
        for (slot in buySlots) gui.setItem(slot.get, displayPriceOfBuy(slot.item))
        for (slot in sellSlots) gui.setItem(slot.get, displayPriceOfSell(slot.item))
        gui.setItem(1 * 9 + 7, allBuyItem())
        gui.setItem(2 * 9 + 7, allSellItem())
        gui.setItem(9 * 1 + 8, ItemClass.playerInfo(player))
        gui.setItem(9 * 2 + 8, backIcon)
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return
        val item = e.currentItem ?: return
        val trade = isSellOrBuy(item) ?: return
        when (trade) {
            Trade.BUY -> buy(item.amount)
            Trade.SELL -> sell(item.amount)
            Trade.ALL_BUY -> maxBuy()
            Trade.ALL_SELL -> allSell()
            Trade.BACK -> shop.openGUI()
        }
    }

    @EventHandler
    fun pickUp(e: PlayerPickupItemEvent) {
        if (e.player != player) return
        if (e.item == item) displaying()
    }

    @EventHandler
    fun drop(e: PlayerDropItemEvent) {
        if (e.player != player) return
        if (e.itemDrop == item) displaying()
    }

    fun buy(amount: Int) {
        buyPrice ?: return
        if (amount <= 0) return
        val price = amount * buyPrice
        val inv = player.inventory
        if (!moneyData.payMoney(item.display(), price.toLong())) return "$prefix §c보유 중인 금액이 부족합니다.".toPlayer(player)
        if (inv.howManyToAdd(item) < amount) return "$prefix §c인벤토리 공간이 부족합니다.".toPlayer(player)
        inv.addItem(item.amount(amount))
        displaying()
        "$prefix $itemDisplay §f을/를 §e$amount §f개 §a구매 §f하였습니다.".toPlayer(player)
        "§e[§f보유 금액§e] §f${moneyData.getMoney().addComma()} §e원".toPlayer(player)
    }

    fun sell(amount: Int) {
        sellPrice ?: return
        if (amount <= 0) return
        var a = amount
        val inv = player.inventory
        if (!inv.hasSameItem(item)) return "$prefix §c보유 중인 $itemDisplay §c이/가 없습니다.".toPlayer(player)
        val map = inv.removeItem(item.amount(a))
        if (map.isNotEmpty()) a -= map[0]!!.amount
        val price = a * sellPrice
        moneyData.addMoney(item.display(), price.toLong())
        displaying()
        "$prefix $itemDisplay §f을/를 §e$a §f개 §9판매 §f하였습니다.".toPlayer(player)
        "§e[§f보유 금액§e] §f${moneyData.getMoney().addComma()} §e원".toPlayer(player)
    }

    fun maxBuy() {
        buyPrice ?: return
        val canSlot = player.inventory.howManyToAdd(item)
        val canMoney = if ((moneyData.getMoney() / buyPrice) >= 2304) 2304
        else (moneyData.getMoney() / buyPrice)
        val canBuy = if (canSlot <= canMoney) canSlot
        else canMoney.toInt()
        buy(canBuy)
    }

    fun allSell() {
        sellPrice ?: return
        val inv = player.inventory
        val has = inv.howManyHasSameItem(item)
        sell(has)
    }

    fun isSellOrBuy(item: ItemStack): Trade? {
        val display = item.itemMeta.displayName
        if (display.contains("뒤로 가기")) return Trade.BACK
        if (display.contains("구매")) {
            if (display.contains("최대")) return Trade.ALL_BUY
            return Trade.BUY
        }
        if (display.contains("판매")) {
            if (display.contains("전부")) return Trade.ALL_SELL
            return Trade.SELL
        }
        return null
    }

    fun displayPriceOfBuy(item: ItemStack): ItemStack? {
        buyPrice ?: return null
        val i = item.clone()
        val amount = i.amount
        if (amount > this.item.type.maxStackSize) return null
        i.display("§a[§f$amount 개 구매§a]")
        i.addLore("§7총 ${(buyPrice * amount).addComma()} 원")
        return i
    }

    fun displayPriceOfSell(item: ItemStack): ItemStack? {
        sellPrice ?: return null
        val i = item.clone()
        val amount = i.amount
        if (amount > this.item.type.maxStackSize) return null
        i.display("§9[§f$amount 개 판매§9]")
        i.addLore("§7총 ${(sellPrice * amount).addComma()} 원")
        return i
    }

    fun allBuyItem(): ItemStack? {
        buyPrice ?: return null
        val item = head(9945).display("§a[§f최대 구매§a]")
        val canSlot = player.inventory.howManyToAdd(item)
        val canMoney = if ((moneyData.getMoney() / buyPrice) >= 2304) 2304
        else (moneyData.getMoney() / buyPrice)
        val canBuy = if (canSlot <= canMoney) canSlot
        else canMoney.toInt()
        item.addLore("§7총 $canBuy 개 구매 가능")
        item.addLore("§7총 ${(buyPrice * canBuy).addComma()} 원")
        return item
    }

    fun allSellItem(): ItemStack? {
        sellPrice ?: return null
        val headItem = head(8973).display("§9[§f전부 판매§9]")
        val has = player.inventory.howManyHasSameItem(item)
        headItem.addLore("§7총 $has 개 판매 가능")
        headItem.addLore("§7총 ${(sellPrice * has).addComma()} 원")
        return headItem
    }

    enum class Trade {
        BUY, SELL, ALL_BUY, ALL_SELL, BACK
    }
}