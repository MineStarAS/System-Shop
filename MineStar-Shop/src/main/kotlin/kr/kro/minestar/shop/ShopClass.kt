package kr.kro.minestar.shop.functions

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.Main.Companion.prefix
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object ShopClass {
    fun create(player: Player, name: String, icon: Material) {
        if (icon == Material.AIR) return "$prefix 아이콘으로 사용할 아이템을 손에 들고 사용하여야 합니다.".toPlayer(player)
        val file = File("${Main.pl.dataFolder}/shopList", "$name.yml")
        if (file.exists()) return "$prefix §c같은 이름의 상점이 이미 존재합니다.".toPlayer(player)
        val data = YamlConfiguration.loadConfiguration(file)
        data["icon"] = icon.name
        data.save(file)
        return "$prefix §a정상적으로 상점이 생성되었습니다!".toPlayer(player)
    }
}