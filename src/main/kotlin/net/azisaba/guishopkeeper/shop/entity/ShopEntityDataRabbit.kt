package net.azisaba.guishopkeeper.shop.entity

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.gui.GuiSlot
import net.azisaba.guishopkeeper.item.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Rabbit

@Serializable
data class ShopEntityDataRabbit(
    private var type: Rabbit.Type = Rabbit.Type.WHITE,
) : ShopEntityDataAgeable() {
    override fun apply(entity: Entity) {
        super.apply(entity)
        if (entity !is Rabbit) {
            error("entity is not type of Rabbit")
        }
        entity.rabbitType = type
    }

    override fun createSettingsGuiSlots(): List<GuiSlot> =
        super.createSettingsGuiSlots() + listOf(
            ShopEntityData.createEnumGuiSlot(ItemBuilder(Material.WHITE_DYE)
                .name("${ChatColor.GREEN}種類: ${ChatColor.WHITE}${type.name}")
                .build(), type) { type = it }
        )
}
