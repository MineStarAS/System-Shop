package kr.kro.minestar.shop.gui

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.data.CommodityItem
import kr.kro.minestar.shop.value.FolderValue
import kr.kro.minestar.utility.file.child
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ShopGUI(override val player: Player, name: String) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        PREVIOUS_PAGE(5, 0, Main.head.item(8902, Material.BLUE_CONCRETE).display("§9[§f이전 페이지§9]")),
        NEXT_PAGE(5, 8, Main.head.item(8899, Material.BLUE_CONCRETE).display("§9[§f다음 페이지§9]")),
        PAGE_NUMBER(5, 4, Main.head.item(11504, Material.GRAY_CONCRETE).display("§7[§f현재 페이지§7]")),
        ;
    }

    override val plugin = Main.plugin
    private val prefix = plugin.prefix

    override val gui: Inventory = InventoryUtil.gui(6, name)

    private val yamlFile = FolderValue.shopFolder().child("$name.yml")
    private val yaml = YamlConfiguration.loadConfiguration(yamlFile)

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

    private fun isOverNumber() = page > (pageKeys.lastOrNull() ?: 0)

    override fun displaying() {
        gui.clear()

        setItems(Button.values())

        if (page < 64) {
            val slot = Button.PAGE_NUMBER
            val pageNumberItem = slot.item.clone().amount(page + 1)
            gui.setItem(slot.getIndex(), pageNumberItem)
        }

        for (slot in 0 until 9 * 5) gui.setItem(slot, CommodityItem(yamlFile, yaml, page, slot).showcaseItem())
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return
        val clickType = e.click

        val clickItem = e.currentItem ?: Material.AIR.item()

        when (getSlot(clickItem, Button.values())) {
            Button.NEXT_PAGE -> {
                if (!e.isLeftClick) return
                page++
                if (clickType == ClickType.SHIFT_LEFT) {
                    if (player.isOp) displaying()
                } else if (isOverNumber()) {
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
                if (clickType == ClickType.LEFT) CommodityItem(yamlFile, yaml, page, e.slot)
                if (clickType == ClickType.SHIFT_LEFT) if (player.isOp) CommodityItemEditGUI(player, this, CommodityItem(yamlFile, yaml, page, e.slot))
            }
        }
    }
}