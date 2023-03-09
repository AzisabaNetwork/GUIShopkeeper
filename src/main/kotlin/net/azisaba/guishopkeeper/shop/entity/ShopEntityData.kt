package net.azisaba.guishopkeeper.shop.entity

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.gui.GuiSlot
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

@Serializable
sealed interface ShopEntityData {
    companion object {
        fun createData(entityType: EntityType) =
            when (entityType) {
                EntityType.VILLAGER -> ShopEntityDataVillager()
                EntityType.RABBIT -> ShopEntityDataRabbit()
                else -> ShopEntityDataDummy
            }

        @JvmStatic
        fun <E : Enum<E>> createEnumGuiSlot(itemStack: ItemStack, currentValue: E, setter: (E) -> Unit) =
            GuiSlot(itemStack) {
                setter(currentValue.javaClass.enumConstants.let {
                    it[(it.indexOf(currentValue) + 1) % it.size]
                })
            }
    }

    fun apply(entity: Entity)

    fun createSettingsGuiSlots(): List<GuiSlot> = emptyList()
}

@Serializable
object ShopEntityDataDummy : ShopEntityData {
    override fun apply(entity: Entity) {
        // do nothing
    }
}

