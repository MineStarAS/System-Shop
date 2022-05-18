package kr.kro.minestar.pack

import kr.kro.minestar.pack.Main.Companion.prefix
import kr.kro.minestar.utility.command.Argument
import kr.kro.minestar.utility.command.FunctionalCommand
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.unit.setFalse
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Command : FunctionalCommand {
    private enum class Arg(override val howToUse: String) : Argument {
        cmd1("[create/remove]"),
        cmd2("[create/remove] <PlayerName>"),
        cmd3("[create/remove] <PlayerName> {Amount}"),
    }

    private enum class OpArg(override val howToUse: String) : Argument {
        test(""),
    }

    override fun commanding(player: CommandSender, cmd: Command, label: String, args: Array<out String>) {
        if (player !is Player) return

        if (args.isEmpty()) return "$prefix $label".toPlayer(player)

        val arg = argument(Arg.values(), args) ?: if (player.isOp) argument(OpArg.values(), args) ?: return else return

        if (!arg.isValid(args)) return "$prefix §c${arg.howToUse(label)}".toPlayer(player)

        when (arg) {
            Arg.cmd1 -> {}
            Arg.cmd2 -> {}
            Arg.cmd3 -> {}

            OpArg.test -> {}
        }
        return
    }

    override fun onTabComplete(player: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String> {
        if (player !is Player) return listOf()

        val list = mutableListOf<String>()

        val arg = argument(Arg.values(), args) ?: if (player.isOp) argument(OpArg.values(), args) else null
        val lastIndex = args.lastIndex
        val last = args.lastOrNull() ?: ""

        fun List<String>.add() {
            for (s in this) if (s.contains(last)) list.add(s)
        }
        fun Array<out Argument>.add() {
            for (s in this) if (s.name.contains(last)) list.add(s.name)
        }
        fun playerAdd() {
            for (s in Bukkit.getOnlinePlayers()) if (s.name.contains(last)) list.add(s.name)
        }

        if (arg == null) {
            Arg.values().add()
            if (player.isOp) OpArg.values().add()
        } else when (arg) {
            Arg.cmd1 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
            }
            Arg.cmd2 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
                2 -> playerAdd()
            }
            Arg.cmd3 -> when (lastIndex) {
                1 -> arg.argList(lastIndex).add()
                2 -> playerAdd()
                3 -> if (last.isEmpty()) list.add(arg.argElement(args))
            }

            OpArg.test -> {}
        }
        return list
    }
}