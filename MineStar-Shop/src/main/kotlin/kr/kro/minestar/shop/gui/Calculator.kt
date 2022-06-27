package kr.kro.minestar.shop.gui

import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.Main.Companion.prefix
import kr.kro.minestar.shop.functions.ItemClass.head
import kr.kro.minestar.shop.functions.gui.Custom
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class Calculator(override val player: Player, val custom: Custom, val isBuy: Boolean) : GUI() {
    override val pl = Main.pl
    val number = listOf(
        head(9271).display("0"),
        head(9270).display("1"),
        head(9269).display("2"),
        head(9268).display("3"),
        head(9267).display("4"),
        head(9266).display("5"),
        head(9265).display("6"),
        head(9264).display("7"),
        head(9263).display("8"),
        head(9262).display("9"),
        head(9243).display("-"),
    )
    val slots = listOf(
        Slot(3, 0, head(8813).display("1")),
        Slot(3, 1, head(8812).display("2")),
        Slot(3, 2, head(8811).display("3")),
        Slot(3, 3, head(8810).display("4")),
        Slot(3, 4, head(8809).display("5")),
        Slot(2, 0, head(8808).display("6")),
        Slot(2, 1, head(8807).display("7")),
        Slot(2, 2, head(8806).display("8")),
        Slot(2, 3, head(8805).display("9")),
        Slot(2, 4, head(8814).display("0")),
        Slot(2, 5, head(9403).display("§cClear")),
        Slot(3, 5, head(9334).display("§cBackSpace")),
        Slot(2, 6, head(8913).display("§9+")),
        Slot(3, 6, head(8950).display("§9x")),
        Slot(2, 7, head(8919).display("§9-")),
        Slot(3, 7, head(8907).display("§9÷")),
        Slot(2, 8, head(21771).display("§aSave")),
        Slot(3, 8, head(9889).display("§a=")),
        Slot(1, 0, Operation.BLANK.item),
    )
    override val gui = Bukkit.createInventory(null, 9 * 4, "[Calculator]").let {
        for (slot in slots) it.setItem(slot.get, slot.item)
        it
    }

    var disInt = if (isBuy) custom.buyPrice ?: 0
    else custom.sellPrice ?: 0
    var operInt = 0
    var oper = Operation.BLANK

    init {
        openGUI()
    }


    override fun displaying() {
        checkOver()
        for (i in 0..17) gui.setItem(i, null)
        val arr1 = disInt.toString().toCharArray()
        var slot = 8
        arr1.reverse()
        for (c in arr1) gui.setItem(slot, toItem(c)).also { --slot }
        gui.setItem(9, oper.item)
        if (oper != Operation.BLANK) {
            val arr2 = operInt.toString().toCharArray()
            slot = 8
            arr2.reverse()
            for (c in arr2) gui.setItem(9 + slot, toItem(c)).also { --slot }
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true
        val item = e.currentItem
        if (e.clickedInventory == e.view.topInventory) {
            if (e.click != ClickType.LEFT) return
            when (item) {
                slots[0].item,
                slots[1].item,
                slots[2].item,
                slots[3].item,
                slots[4].item,
                slots[5].item,
                slots[6].item,
                slots[7].item,
                slots[8].item,
                slots[9].item -> addNumber(ChatColor.stripColor(item.itemMeta.displayName)!!.toInt())
                slots[10].item -> clearNumber()
                slots[11].item -> removeNumber()
                slots[12].item -> zetOper(Operation.ADD)
                slots[13].item -> zetOper(Operation.MULTIPLE)
                slots[14].item -> zetOper(Operation.SUB)
                slots[15].item -> zetOper(Operation.DIVISION)
                slots[16].item -> save()
                slots[17].item -> calculate()
            }
//            " ".toServer()
//            "disInt : $disInt".toServer()
//            "operInt : $operInt".toServer()
//            "oper : $oper".toServer()
        }
    }

    fun addNumber(int: Int) {
        var s: String = if (oper == Operation.BLANK) disInt.toString()
        else operInt.toString()
        if (s.replace("-", "").toCharArray().size == 8) return
        s += int.toString()
        if (oper == Operation.BLANK) disInt = s.toInt()
        else operInt = s.toInt()
        displaying()
    }

    fun removeNumber() {
        val s: String = if (oper == Operation.BLANK) disInt.toString()
        else operInt.toString()
        if (s.replace("-", "").toCharArray().size == 1) {
            if (oper == Operation.BLANK) disInt = 0
            else operInt = 0
            return displaying()
        }
        val l = s.toCharArray().toMutableList()
        var ss = ""
        for ((i, c) in l.withIndex()) if (i != l.size - 1) ss += c
        if (oper == Operation.BLANK) disInt = ss.toInt()
        else operInt = ss.toInt()
        displaying()
    }

    fun clearNumber() {
        if (oper == Operation.BLANK) disInt = 0
        else operInt = 0
        displaying()
    }

    fun checkOver() {
        if (99999999 < disInt) disInt = 99999999
        if (disInt < -99999999) disInt = -99999999
        if (99999999 < operInt) operInt = 99999999
        if (operInt < 0) operInt = 0
    }

    fun zetOper(oper: Operation) {
        this.oper = oper
        displaying()
    }

    fun calculate() {
        if (oper == Operation.BLANK) return
        when (oper) {
            Operation.ADD -> disInt += operInt
            Operation.SUB -> disInt -= operInt
            Operation.MULTIPLE -> disInt *= operInt
            Operation.DIVISION -> disInt /= operInt
        }

        operInt = 0
        zetOper(Operation.BLANK)
        displaying()
    }

    fun save() {
        calculate()
        if (isBuy) {
            if (custom.sellPrice == null) custom.buyPrice = disInt
            else if (disInt < custom.sellPrice!!) {
                custom.buyPrice = custom.sellPrice
                "$prefix §c구매 가격이 판매 가격보다 낮을 수 없습니다.".toPlayer(player)
            } else custom.buyPrice = disInt
        } else {
            if (custom.buyPrice == null) custom.sellPrice = disInt
            else if (custom.buyPrice!! < disInt) {
                custom.sellPrice = custom.buyPrice
                "$prefix §c판매 가격이 구매 가격보다 높을 수 없습니다.".toPlayer(player)
            } else custom.sellPrice = disInt
        }
        custom.openGUI()
    }

    fun toItem(char: Char): ItemStack {
        when (char) {
            '0' -> return number[0]
            '1' -> return number[1]
            '2' -> return number[2]
            '3' -> return number[3]
            '4' -> return number[4]
            '5' -> return number[5]
            '6' -> return number[6]
            '7' -> return number[7]
            '8' -> return number[8]
            '9' -> return number[9]
            '-' -> return number[10]
        }
        return number[10]
    }

    enum class Operation(val item: ItemStack) {
        BLANK(head(13394).display(" ")),
        ADD(head(10101).display("§b+")),
        SUB(head(10107).display("§b-")),
        MULTIPLE(head(10138).display("§bx")),
        DIVISION(head(10095).display("§b÷")),
    }
}

