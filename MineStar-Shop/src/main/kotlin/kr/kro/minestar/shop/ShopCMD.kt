package kr.kro.minestar.shop

import kr.kro.minestar.shop.Main.Companion.prefix
import kr.kro.minestar.shop.functions.ShopClass
import kr.kro.minestar.shop.gui.Shop
import kr.kro.minestar.shop.gui.ShopList
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.unit.setFalse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.io.File

object ShopCMD : CommandExecutor, TabCompleter {

    private enum class Arg { create, open }
    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (!player.isOp) return false
        if (args.isEmpty()) {
            ShopList(player)
            return false
        } else when (args.first()) {
            Arg.create.name -> {
                if (args.size != 2) return "$prefix §c/shop ${args.first()} <ShopName>".toPlayer(player).setFalse()
                ShopClass.create(player, args.last(),player.inventory.itemInMainHand.type)
            }
            Arg.open.name -> {
                if (args.size != 2) return "$prefix §c/shop ${args.first()} <ShopName>".toPlayer(player).setFalse()
                Shop(player, args.last())
            }
        }
        return false
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        if (!p.isOp) return list
        val last = args.size - 1
        if (args.size == 1) {
            for (s in Arg.values()) if (s.name.contains(args.last())) list.add(s.name)
        }
        if (args.size > 1) when (args.first()) {
            Arg.create.name -> {}
            Arg.open.name -> if (args.size == 2) for (s in File("${Main.pl.dataFolder}/shopList").listFiles())
                if (s.name.replace(".yml", "").contains(args[last])) list.add(s.name.replace(".yml", ""))
        }
        return list
    }

}