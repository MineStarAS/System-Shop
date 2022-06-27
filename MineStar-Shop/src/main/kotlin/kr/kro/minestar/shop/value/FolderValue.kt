package kr.kro.minestar.shop.value

import kr.kro.minestar.shop.function.ConfigClass
import org.bukkit.entity.Player
import java.io.File

object FolderValue {
    private fun dataFolder() = ConfigClass().dataSaveFolder

    private val playerFolder = File(dataFolder(), "players").apply { if (!exists()) mkdir() }
    fun playerFolder(player: Player) = File(playerFolder, "${player.uniqueId}").apply { if (!exists()) mkdir() }


    fun shopFolder() = File(dataFolder(), "shopList").apply { if (!exists()) mkdir() }

    fun logFolder() = File(dataFolder(), "log").apply { if (!exists()) mkdir() }
}