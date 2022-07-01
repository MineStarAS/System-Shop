package kr.kro.minestar.shop.gui

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.shop.Command.warningScript
import kr.kro.minestar.shop.Main
import kr.kro.minestar.shop.data.CommodityItem
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CommodityItemEditGUI(override val player: Player, private val shopGUI: ShopGUI, private val commodityItem: CommodityItem) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        CLEAR_COMMODITY_ITEM(0, 6, Main.head.item(9397, Material.RED_CONCRETE).display("§c[§f아이템 삭제§c]")),

        SET_BUY_PRICE(1, 0, Main.head.item(9944, Material.LIME_CONCRETE).display("§a[§f구매 가격 설정§a]")),
        SET_BUY_CURRENCY(1, 1, Main.head.item(9943, Material.LIME_CONCRETE).display("§a[§f구매 화폐 설정§a]")),
        CLEAR_BUY_PRICE(1, 2, Main.head.item(9404, Material.RED_CONCRETE).display("§c[§f구매 가격 삭제§c]")),
        CLEAR_BUY_CURRENCY(1, 3, Main.head.item(9403, Material.RED_CONCRETE).display("§c[§f구매 화폐 삭제§c]")),

        SET_SELL_PRICE(1, 4, Main.head.item(8955, Material.BLUE_CONCRETE).display("§9[§f판매 가격 설정§9]")),
        SET_SELL_CURRENCY(1, 5, Main.head.item(8971, Material.BLUE_CONCRETE).display("§9[§f판매 화폐 설정§9]")),
        CLEAR_SELL_PRICE(1, 6, Main.head.item(9387, Material.RED_CONCRETE).display("§c[§f판매 가격 삭제§c]")),
        CLEAR_SELL_CURRENCY(1, 7, Main.head.item(9403, Material.RED_CONCRETE).display("§c[§f판매 화폐 삭제§c]")),

        GO_TO_BACK_GUI(1, 8, Main.head.item(9982, Material.LIGHT_GRAY_CONCRETE).display("§7[§f뒤로 가기§7]")),
        ;
    }

    override val plugin = Main.plugin
    override val gui = InventoryUtil.gui(2, "상품 아이템 설정")

    private val nullItem = ItemStack(Material.STRUCTURE_VOID)
        .display("§7[§f비어있음§7]")
        .addLore("§7자신의 인벤토리에 있는 아이템을")
        .addLore("§7더블클릭하여 상점에 등록합니다.")


    init {
        openGUI()
    }

    override fun displaying() {
        gui.clear()

        gui.setItem(4, commodityItem.editItem() ?: nullItem)

        setItems(Button.values())
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        val clickItem = e.currentItem ?: return

        if (e.clickedInventory == e.view.topInventory) {
            if (e.click != ClickType.LEFT) return

            when (getSlot(clickItem, Button.values())) {
                Button.CLEAR_COMMODITY_ITEM -> {
                    commodityItem.item(null)
                    displaying()
                }
                Button.SET_BUY_PRICE -> CalculatorGUI(player, this, EditType.BUY_PRICE)
                Button.SET_BUY_CURRENCY -> CurrenciesGUI(player, this, EditType.BUY_CURRENCY)

                Button.CLEAR_BUY_PRICE -> {
                    commodityItem.buyPrice(price = null)
                    displaying()
                }
                Button.CLEAR_BUY_CURRENCY -> {
                    commodityItem.buyPrice(currency = null)
                    displaying()
                }

                Button.SET_SELL_PRICE -> CalculatorGUI(player, this, EditType.SELL_PRICE)
                Button.SET_SELL_CURRENCY -> CurrenciesGUI(player, this, EditType.SELL_CURRENCY)

                Button.CLEAR_SELL_PRICE -> {
                    commodityItem.sellPrice(price = null)
                    displaying()
                }
                Button.CLEAR_SELL_CURRENCY -> {
                    commodityItem.sellPrice(currency = null)
                    displaying()
                }
                Button.GO_TO_BACK_GUI -> shopGUI.openGUI()
            }
        } else if (e.clickedInventory == e.view.bottomInventory) {
            if (commodityItem.item() != null) return
            if (e.click != ClickType.DOUBLE_CLICK) return
            commodityItem.item(clickItem)
            displaying()
        }
    }

    /**
     * Outside function
     */
    fun edit(long: Long, editType: EditType) {
        when (editType) {

            EditType.BUY_PRICE -> {
                if (long < 0) return "0 보다 작을 수 없습니다.".warningScript(player)
                if (commodityItem.sellPrice().price != null)
                    if (long < commodityItem.sellPrice().price!!)
                        "판매 가격보다 작을 경우 화폐 복사가 일어날 수 있으니,\n 주의하시기 바랍니다.".warningScript(player)
                commodityItem.buyPrice(long)
            }

            EditType.SELL_PRICE -> {
                if (long < 0) return "0 보다 작을 수 없습니다.".warningScript(player)
                if (commodityItem.buyPrice().price != null)
                    if (long > commodityItem.buyPrice().price!!)
                        "구매 가격보다 클 경우 화폐 복사가 일어날 수 있으니,\n 주의하시기 바랍니다.".warningScript(player)
                commodityItem.sellPrice(long)
            }

            else -> return
        }
        displaying()
    }

    fun edit(currency: Currency, editType: EditType) {
        when (editType) {

            EditType.BUY_CURRENCY -> commodityItem.buyPrice(currency)
            EditType.SELL_CURRENCY -> commodityItem.sellPrice(currency)

            else -> return
        }
        displaying()
    }


    enum class EditType {
        BUY_PRICE,
        BUY_CURRENCY,
        SELL_PRICE,
        SELL_CURRENCY,
    }
}