package kr.kro.minestar.pack

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f[§9PLUGIN§f]"
    }

    override fun onEnable() {
        pl = this
        logger.info("$prefix §aEnable")
        getCommand("cmd")?.setExecutor(CMD())
    }

    override fun onDisable() {
    }
}