package kr.kro.minestar.shop.functions.gui

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.Main.Companion.prefix
import kr.kro.minestar.shop.functions.ItemClass.head
import kr.kro.minestar.shop.gui.Calculator
import kr.kro.minestar.shop.gui.Shop
import kr.kro.minestar.utility.event.disable
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class Custom(override val player: Player, var item: ItemStack?, val shop: Shop) : GUI {
    override val pl = Main.pl

    val file = shop.yamlFile
    val data = shop.yaml
    override val gui = Bukkit.createInventory(null, 9, "[CUSTOMIZING ITEM]")

    var buyPrice = if (item != null) shop.buyPrice(shop.page, shop.slot)
    else null
    var sellPrice = if (item != null) shop.sellPrice(shop.page, shop.slot)
    else null

    val nullItem = ItemStack(Material.STRUCTURE_VOID).also {
        it.display("[§7비어있음§f]")
        it.addLore("§7자신의 인벤토리에 있는 아이템을")
        it.addLore("§7더블클릭하여 상점에 등록합니다.")
    }

    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()
        gui.setItem(3, buyItem())
        gui.setItem(5, sellItem())
        gui.setItem(8, head(21771).display("§aSave"))
        if (item == null) gui.setItem(4, nullItem)
        else gui.setItem(4, item)
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        if (e.clickedInventory == e.view.topInventory) {
            when (e.currentItem) {
                buyItem() -> when (e.click) {
                    ClickType.LEFT -> Calculator(player, this, true)
                    ClickType.SHIFT_LEFT -> setCannotBuy()
                }

                sellItem() -> when (e.click) {
                    ClickType.LEFT -> Calculator(player, this, false)
                    ClickType.SHIFT_LEFT -> setCannotSell()
                }

                item -> if (e.click == ClickType.SHIFT_LEFT) removeItem()

                gui.getItem(8) -> if (e.click == ClickType.LEFT) {
                    save()
                    shop.openGUI()
                }
            }
        } else if (e.clickedInventory == e.view.bottomInventory) {
            if (e.click != ClickType.LEFT) return
            if (e.currentItem != null && gui.getItem(4) != nullItem) return "$prefix §c이미 아이템이 등록되어 있습니다.".toPlayer(player)
            if (item == null && e.currentItem == null) return "$prefix §c이미 아이템이 비어있습니다.".toPlayer(player)
            item = e.currentItem?.clone()
            displaying()
            return "$prefix 아이템이 §a등록§f되었습니다!".toPlayer(player)
        }
    }

    @EventHandler
    override fun closeGUI(e: InventoryCloseEvent) {
        if (e.player != player) return
        if (e.inventory != gui) return
        save()
        disable()
    }

    fun removeItem() {
        item = null
        displaying()
        return "$prefix 아이템을 삭제하였습니다".toPlayer(player)
    }

    fun setCannotBuy() {
        buyPrice = null
        displaying()
        return "$prefix §a구매§f가 §c금지§f되었습니다.".toPlayer(player)
    }

    fun setCannotSell() {
        sellPrice = null
        displaying()
        return "$prefix §9판매§f가 §c금지§f되었습니다.".toPlayer(player)
    }

    fun save() {
        val path = "${shop.page}.${shop.slot}"
        if (item == null) {
            data["$path.item"] = null
            data["$path.buy"] = null
            data["$path.sell"] = null
            data.save(file)
            return
        }
        item!!.amount = 1
        data["$path.item"] = item
        data["$path.buy"] = buyPrice
        data["$path.sell"] = sellPrice
        data.save(file)
    }

    fun buyItem(): ItemStack {
        return head(40476).also {
            if (buyPrice == null) it.display("§c[§f구매 불가§c]")
            else it.display("${shop.tagBuy} ${buyPrice!!.addComma()} ${shop.unit}")
            it.addLore("§7[좌클릭] 가격 설정")
            it.addLore("§7[쉬프트 좌클릭] 거래불가 설정")
        }
    }

    fun sellItem(): ItemStack {
        return head(40473).also {
            if (sellPrice == null) it.display("§c[§f판매 불가§c]")
            else it.display("${shop.tagSell} ${sellPrice!!.addComma()} ${shop.unit}")
            it.addLore("§7[좌클릭] 가격 설정")
            it.addLore("§7[쉬프트 좌클릭] 거래불가 설정")
        }
    }
}