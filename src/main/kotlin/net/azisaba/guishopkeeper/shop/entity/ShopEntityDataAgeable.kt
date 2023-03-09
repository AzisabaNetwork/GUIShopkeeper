package net.azisaba.guishopkeeper.shop.entity

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.gui.GuiSlot
import net.azisaba.guishopkeeper.item.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Ageable
import org.bukkit.entity.Entity

@Serializable
sealed class ShopEntityDataAgeable : ShopEntityData {
    private var baby: Boolean = false

    override fun apply(entity: Entity) {
        if (entity !is Ageable) {
            error("entity is not type of Ageable")
        }
        entity.ageLock = true
        if (baby) {
            entity.setBaby()
        } else {
            entity.setAdult()
        }
    }

    override fun createSettingsGuiSlots(): List<GuiSlot> = listOf(
        GuiSlot(
            ItemBuilder(Material.CAT_SPAWN_EGG)
                .name(if (baby) "${ChatColor.GREEN}大人にする" else "${ChatColor.RED}子供にする")
                .build()
        ) { baby = !baby }
    )
}