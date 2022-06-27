package kr.kro.minestar.pack.function.event

import kr.kro.minestar.pack.Main.Companion.plugin
import kr.kro.minestar.utility.event.enable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object Event : Listener {
    init {
        enable(plugin)
    }

    @EventHandler
    fun join(e: PlayerJoinEvent) {
    }
}