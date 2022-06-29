package kr.kro.minestar.shop

import kr.kro.minestar.utility.item.Head
import kr.kro.minestar.utility.main.FunctionalJavaPlugin
import kr.kro.minestar.utility.string.StringColor
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Main : FunctionalJavaPlugin() {
    companion object {
        lateinit var head: Head
        lateinit var plugin: FunctionalJavaPlugin
    }

    override fun onEnable() {
        plugin = this
        prefix = "§9Shop"
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

    fun String.warningScript(player: Player) = this.script(prefix, StringColor.RED).toPlayer(player)
}