package kr.kro.minestar.shop.gui

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.value.FolderValue
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.item.flagAll
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.remove
import kr.kro.minestar.utility.string.unColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ShopList(override val player: Player) : GUI() {
    override val plugin = Main.plugin
    private val shopList = FolderValue.shopFolder().listFiles()
    private val guiSize = shopList.size / 9 + 9

    override val gui = Bukkit.createInventory(null, guiSize, "[상점 목록]")

    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()
        for (shop in shopList) {
            val data = YamlConfiguration.loadConfiguration(shop)
            val icon = Material.valueOf(data.getString("icon") ?: "STONE")
            val name = shop.name.remove(".yml")
            val item = icon.item().display(name).flagAll()
            gui.addItem(item)
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
        val name = clickItem.display().unColor()
        Shop(player, name)
    }
}