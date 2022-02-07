package kr.kro.minestar.pack

import kr.kro.minestar.pack.Main.Companion.prefix
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object CMD : CommandExecutor, TabCompleter {
    private enum class Arg { cmd1, cmd2, cmd3 }

    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) {
            "$prefix $label".toPlayer(player)
            return false
        }
        when (args.first()) {
            Arg.cmd1.name -> {}
            Arg.cmd2.name -> {}
            Arg.cmd3.name -> {}
        }
        return false
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        if (args.size == 1) {
            for (s in Arg.values()) if (s.name.contains(args.last())) list.add(s.name)
        }
        if (args.size > 1) when (args.first()) {
            Arg.cmd1.name -> {}
            Arg.cmd2.name -> {}
            Arg.cmd3.name -> {}
        }
        return list
    }
}