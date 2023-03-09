package net.azisaba.guishopkeeper.shop

import kotlinx.serialization.Serializable

@Serializable
sealed interface ShopTradeData

@Serializable
object ShopTradeDataEmpty : ShopTradeData

@Serializable
data class ShopTradeDataItem(
    var result: ShopItem,
    val price: MutableList<ShopItem> = mutableListOf(),
) : ShopTradeData {
    /**
     * Check if the price is empty
     */
    fun isPriceEmpty(): Boolean =
        price.isEmpty() || price.all { it is ShopItemAir }

    /**
     * Set price item at provided index
     */
    fun setPriceAt(index: Int, trade: ShopItem) {
        // fill with empty item if needed
        while (price.size <= index) {
            price.add(ShopItemAir)
        }
        price[index] = trade
    }
}
