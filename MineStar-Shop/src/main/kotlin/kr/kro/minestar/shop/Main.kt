package kr.kro.minestar.shop

import kr.kro.minestar.utility.item.Head
import kr.kro.minestar.utility.main.FunctionalJavaPlugin
import org.bukkit.Bukkit

class Main : FunctionalJavaPlugin() {
    companion object {
        lateinit var head: Head
        lateinit var plugin: FunctionalJavaPlugin
    }

    override fun onEnable() {
        plugin = this
        prefix = "§Shop"
        head = Head(this)
//        saveResource("headItem.yml", true)
        getCommand("shop")?.setExecutor(Command)
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) try {
            player.closeInventory()
        } catch (_: Exception) {
        }
    }
}