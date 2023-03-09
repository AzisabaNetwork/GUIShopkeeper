package net.azisaba.guishopkeeper.shop

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.shop.entity.ShopEntityData
import net.azisaba.guishopkeeper.util.SerializableLocation
import net.azisaba.guishopkeeper.util.lastIndexOf
import org.bukkit.entity.EntityType
import java.util.UUID

@Serializable
data class ShopData(
    var name: String,
    var location: SerializableLocation,
    var entityType: EntityType,
    var entityData: ShopEntityData = ShopEntityData.createData(entityType),
    val trades: MutableList<ShopTradeData> = mutableListOf(),
    var allowFreeTrade: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
) {
    /**
     * Set trade at provided index
     */
    fun setTradeAt(index: Int, trade: ShopTradeData) {
        // fill with empty trades if needed
        while (trades.size <= index) {
            trades.add(ShopTradeDataEmpty)
        }
        trades[index] = trade
    }

    fun trimTrades() {
        val lastNonEmpty = trades.lastIndexOf { it !is ShopTradeDataEmpty }
        if (lastNonEmpty == -1) {
            trades.clear()
        } else {
            trades.subList(lastNonEmpty + 1, trades.size).clear()
        }
    }
}
