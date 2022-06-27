package kr.kro.minestar.pack

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
        prefix = "§9Plugin"
        head = Head(this)
//        saveResource("headItem.yml", true)
        getCommand("currency")?.setExecutor(Command)
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) try {
            player.closeInventory()
        } catch (_: Exception) {
        }
    }
}