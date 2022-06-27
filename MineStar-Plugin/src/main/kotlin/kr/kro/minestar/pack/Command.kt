package kr.kro.minestar.pack

import kr.kro.minestar.pack.function.ConfigClass
import kr.kro.minestar.pack.value.PermissionValue
import kr.kro.minestar.utility.command.*
import kr.kro.minestar.utility.string.toPlayer

object Command : FunctionalCommand {

    enum class Arg : Argument {
        cmd1(listOf("커맨드1"), "<ValueType>", ArgumentPermission()),
        cmd2(listOf("커맨드2"), "<ValueType> [Value1/Value2]", PermissionValue.default),
        cmd3(listOf("커맨드3"), "<ValueType> [Value1/Value2] {Value}", PermissionValue.admin),
        ;

        override val howToUse: String
        override val permission: ArgumentPermission
        override val aliases: List<String>?

        constructor(howToUse: String, permission: ArgumentPermission) {
            this.howToUse = howToUse
            this.permission = permission
            this.aliases = null
        }

        constructor(aliases: List<String>, howToUse: String, permission: ArgumentPermission) {
            this.howToUse = howToUse
            this.permission = permission
            this.aliases = aliases
        }
    }

    override val plugin = Main.plugin
    override val arguments = Arg.values()

    override fun isSimplePermission() = ConfigClass().simplePermission

    override fun commanding(data: CommandData, args: Array<out String>) {
        if (!data.valid) return
        val player = data.player ?: return "플레이어가 아닙니다.".warningScript(data.sender)

        when (data.argument) {
            null -> plugin.name.toPlayer(player)
            Arg.cmd1 -> {}
            Arg.cmd2 -> {}
            Arg.cmd3 -> {}
        }
        return
    }

    override fun tabComplete(data: TabCompleteData, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()

        val last = data.last
        val lastIndex = data.lastIndex

        if (!data.valid) return list

        when (val argument = data.argument) {
            /** First argument */
            null -> when (lastIndex) {
                0 -> Arg.values().add(data.sender, list, last, this)
            }
            /** Other argument */
            Arg.cmd1 -> when(lastIndex) {
                1 -> argument.add(list, last, lastIndex)
            }
            Arg.cmd2 -> when(lastIndex) {
                1 -> argument.add(list, last, lastIndex)
                2 -> argument.argList(lastIndex).add(list, last)
            }
            Arg.cmd3 -> when(lastIndex) {
                1 -> argument.add(list, last, lastIndex)
                2 -> argument.argList(lastIndex).add(list, last)
                3 -> argument.add(list, last, lastIndex)
            }
        }

        return list
    }
}