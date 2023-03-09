package net.azisaba.guishopkeeper.shop

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.shop.entity.ShopEntityData
import net.azisaba.guishopkeeper.util.SerializableLocation
import org.bukkit.entity.EntityType
import java.util.UUID

@Serializable
data class ShopData(
    var name: String,
    var location: SerializableLocation,
    var entityType: EntityType,
    var entityData: ShopEntityData = ShopEntityData.createData(entityType),
    val trades: MutableList<ShopTradeData> = mutableListOf(),
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
}
