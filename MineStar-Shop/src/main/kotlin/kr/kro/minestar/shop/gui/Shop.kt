package kr.kro.minestar.shop.gui

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.data.CommodityItem
import kr.kro.minestar.shop.functions.gui.Custom
import kr.kro.minestar.shop.functions.gui.TradeGUI
import kr.kro.minestar.shop.value.FolderValue
import kr.kro.minestar.utility.file.child
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Shop(override val player: Player, name: String) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        PREVIOUS_PAGE(5, 0, Main.head.item(8902, Material.BLUE_CONCRETE).display("§9[이전 페이지]")),
        NEXT_PAGE(5, 8, Main.head.item(8899, Material.BLUE_CONCRETE).display("§7[다음 페이지]")),
        PAGE_NUMBER(5, 4, Main.head.item(11504, Material.GRAY_CONCRETE).display("§9[현재 페이지]")),
        ;
    }

    override val plugin = Main.plugin
    private val prefix = plugin.prefix

    val yamlFile = FolderValue.shopFolder().child("$name.yml")
    val yaml = YamlConfiguration.loadConfiguration(yamlFile)

    init {
        if (!yamlFile.exists()) "$prefix §e$name §c이/라는 상점을 찾을 수 없습니다.".toPlayer(player)
        else openGUI()
    }

    /**
     * Page function
     */
    private var page: Int = 0

    private val pageKeys = pageKeys().toList()
    private fun pageKeys(): Set<Int> {
        val set = hashSetOf<Int>()
        val keys = yaml.getKeys(false)
        for (key in keys) {
            val int = key.toIntOrNull() ?: continue
            set.add(int)
        }
        return set.sorted().toSet()
    }

    private fun isOverNumber() = page > pageKeys.last()

    private val tagBuy = "§a[§f구매 가격§a] §f"
    private val tagSell = "§9[§f판매 가격§9] §f"
    private val unit = "원"


    override var gui: Inventory = Bukkit.createInventory(null, 9 * 6, yaml.getString("Name") ?: name)

    override fun displaying() {
        gui.clear()

        setItems(Button.values())

        if (page < 64) {
            val slot = Button.PAGE_NUMBER
            val pageNumberItem = slot.item.clone().amount(page + 1)
            gui.setItem(slot.getIndex(), pageNumberItem)
        }

        for (slot in 0..9 * 5) {
            val commodityItem = CommodityItem(yamlFile, yaml, page, slot)
            commodityItem.item ?: continue

            fun itemConvert(commodityItem : CommodityItem): ItemStack? {
                val item = commodityItem.item?.clone() ?: return null
                item.amount(1)
                item.addLore(" ")
                if (buyPrice != null) item.addLore("$tagBuy ${buyPrice.addComma()} $unit")
                else item.addLore("§c[§f구매 불가§c]")
                if (sellPrice != null) item.addLore("$tagSell ${sellPrice.addComma()} $unit")
                else item.addLore("§c[§f판매 불가§c]")
                return item
            }
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return
        val clickType = e.click

        val clickItem = e.currentItem ?: return
        val slot = getSlot(clickItem, Button.values())

        when (slot) {
            Button.NEXT_PAGE -> {
                if (!e.isLeftClick) return
                page++
                if (clickType == ClickType.SHIFT_LEFT) if (player.isOp) displaying()
                if (!isOverNumber()) {
                    page--
                    return
                }
                displaying()
            }
            Button.PREVIOUS_PAGE -> {
                if (!e.isLeftClick) return
                page--
                if (page < 0) {
                    page++
                    return
                }
                displaying()
            }

            Button.PAGE_NUMBER -> {}

            null -> {

            }
        }

        if (this.isEditing) {
            when (e.click) {
                ClickType.LEFT -> {
                    if (e.slot !in 0 until 9 * 5) return
                    slot = e.slot
                    Custom(player, item(page, e.slot), this)
                    return
                }
            }
            return
        }

        when (e.click) {
            ClickType.LEFT -> {
                clickItem ?: return
                val dataItem = item(page, e.slot) ?: return
                if (buyPrice(page, e.slot) == null && sellPrice(page, e.slot) == null) return
                TradeGUI(player, this, dataItem, buyPrice(page, e.slot), sellPrice(page, e.slot))
            }
        }
    }
}