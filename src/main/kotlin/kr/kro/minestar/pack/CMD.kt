package kr.kro.minestar.pack

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
        if (args.isEmpty()) "CMD".toPlayer(player).also { return false }
        when (args[0]) {
            Arg.cmd1.name -> {}
            Arg.cmd2.name -> {}
            Arg.cmd3.name -> {}
        }
        return false
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        val arg = mutableListOf<String>()
        if (args.size == 1) {
            for (v in enumValues<Arg>()) arg.add(v.name)
            for (s in arg) if (s.contains(args.last())) list.add(s)
        }
        if (args.size > 1) when (args[0]) {
            Arg.cmd1.name -> {}
            Arg.cmd2.name -> {}
            Arg.cmd3.name -> {}
        }
        return list
    }
}