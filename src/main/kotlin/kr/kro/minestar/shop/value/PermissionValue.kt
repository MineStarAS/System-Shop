package kr.kro.minestar.shop.value

import kr.kro.minestar.shop.Command.plugin
import kr.kro.minestar.utility.command.ArgumentPermission

object PermissionValue {

    val default = ArgumentPermission(plugin, "default", false)

    /**
     * Admin permission
     */
    val admin = ArgumentPermission(plugin, "admin", true)
}