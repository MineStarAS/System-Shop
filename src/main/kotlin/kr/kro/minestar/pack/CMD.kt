package kr.kro.minestar.pack

import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object CMD : CommandExecutor, TabCompleter {
    private val args0 = listOf("cmd1", "cmd2", "cmd3")
    override fun onCommand(player: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (player !is Player) return false
        if (args.isEmpty()) "CMD".toPlayer(player).also { return false }
        when (args[0]) {
            args0[0] -> {}
            args0[1] -> {}
            args0[2] -> {}
        }
        return false
    }

    override fun onTabComplete(p: CommandSender, cmd: Command, alias: String, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()
        val endNumber = args.size - 1
        if (args.size == 1) for (s in args0) if (s.contains(args[endNumber])) list.add(s)
        if (args.size > 1) when (args[0]) {
            args0[0] -> {}
            args0[1] -> {}
            args0[2] -> {}
        }
        return list
    }

}