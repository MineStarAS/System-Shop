package kr.kro.minestar.shop.data

import kr.kro.minestar.currency.data.Currency

class CurrencyPrice (val currency: Currency?, val price: Long?) {

    fun isValid() = currency != null && price != null

    override fun toString() = "§f$price §6$currency"
}