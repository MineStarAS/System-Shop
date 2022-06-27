package kr.kro.minestar.shop.functions

import kr.kro.minestar.money.data.DataClass
import kr.kro.minestar.shop.Main
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.setUnderBar
import me.arcaniax.hdb.api.HeadDatabaseAPI
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object ItemClass {
    val files: Array<File> = File(Main.pl.dataFolder.toString()).listFiles()!!

    fun getFileList(): MutableList<String> {
        val list = mutableListOf<String>()
        for (file in files) {
            if (!file.name.contains("config") && !file.name.contains("shopList")) {
                if (file.name.contains(".yml")) list.add(file.name)
                else list.add("/" + file.name)
            }
        }
        return list
    }

    fun getFileList(fileArray: Array<File>): List<String> {
        val list = mutableListOf<String>()
        for (file in fileArray) {
            if (file.name.contains(".yml")) list.add(file.name)
            else list.add("/" + file.name)
        }
        return list
    }

    fun getFolder(fileArray: Array<File>, folderName: String): Array<File> {
        val fileNameList = getFileList(fileArray)
        var array: Array<File> = arrayOf()
        if (!fileNameList.contains(folderName.folderNameReplace())) return arrayOf()
        for (f in fileArray) if (f.name == folderName.folderNameReplace()) {
            array = f.listFiles()
            break
        }
        return array
    }

    fun String.folderNameReplace(): String {
        return this.replace("/", "").replace("\\", "")
    }

    fun playerInfo(player: Player): ItemStack? {
        val moneyData = DataClass.moneyData(player) ?: return null
        val item = ItemStack(Material.PLAYER_HEAD)
        val meta = item.itemMeta as SkullMeta
        meta.owningPlayer = player
        item.itemMeta = meta
        item.display("§6[§e${player.name} §f님의 소유금§6]")
        item.addLore("§7${moneyData.getMoney().addComma()} 원")
        return item
    }

    fun head(id: Int) = HeadDatabaseAPI().getItemHead("$id") ?: Material.BARRIER.item().display("§c해당 ID의 머리가 없습니다")
}