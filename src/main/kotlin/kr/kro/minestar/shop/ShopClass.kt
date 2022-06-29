package kr.kro.minestar.shop

import kr.kro.minestar.shop.Command.warningScript
import kr.kro.minestar.shop.value.FolderValue
import kr.kro.minestar.utility.file.child
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object ShopClass {
    fun create(player: Player, name: String, icon: Material) {
        if (icon == Material.AIR) return "아이콘으로 사용할 아이템을 손에 들고 사용하여야 합니다.".warningScript(player)
        val file = FolderValue.shopFolder().child("$name.yml")
        if (file.exists()) return "같은 이름의 상점이 이미 존재합니다.".warningScript(player)
        val yaml = YamlConfiguration.loadConfiguration(file)
        yaml["icon"] = icon.name
        yaml.save(file)
        return "§a정상적으로 상점이 생성되었습니다!".script(Main.plugin.prefix).toPlayer(player)
    }
}