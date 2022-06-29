package kr.kro.minestar.shop

import kr.kro.minestar.shop.function.ConfigClass
import kr.kro.minestar.shop.gui.ShopListGUI
import kr.kro.minestar.shop.value.PermissionValue
import kr.kro.minestar.utility.command.*
import kr.kro.minestar.utility.string.toPlayer

object Command : FunctionalCommand {

    enum class Arg : Argument {
        list(listOf("목록"), "", ArgumentPermission()),
        create(listOf("생성"), "<ShopName>", ArgumentPermission()),
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

    override fun isSimplePermission() = ConfigClass.simplePermission()

    override fun commanding(data: CommandData, args: Array<out String>) {
        if (!data.valid) return
        val player = data.player ?: return "플레이어가 아닙니다.".warningScript(data.sender)

        when (data.argument) {
            null -> plugin.name.toPlayer(player)

            Arg.list -> ShopListGUI(player)
            Arg.create -> {
                val name = args[1]
                val icon = player.inventory.itemInMainHand.type
                ShopClass.create(player, name, icon)
            }

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
            Arg.create -> when(lastIndex) {
                1 -> argument.add(list, last, lastIndex)
            }
        }

        return list
    }
}