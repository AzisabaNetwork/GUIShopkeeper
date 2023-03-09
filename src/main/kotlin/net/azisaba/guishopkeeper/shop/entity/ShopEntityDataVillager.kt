package net.azisaba.guishopkeeper.shop.entity

import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.gui.GuiSlot
import net.azisaba.guishopkeeper.item.ItemBuilder
import net.azisaba.guishopkeeper.shop.VillagerExperience
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Villager

@Serializable
data class ShopEntityDataVillager(
    private var profession: Villager.Profession = Villager.Profession.NONE,
    private var type: Villager.Type = Villager.Type.PLAINS,
    private var experience: VillagerExperience = VillagerExperience.Novice,
) : ShopEntityDataAgeable() {
    override fun apply(entity: Entity) {
        super.apply(entity)
        if (entity !is Villager) {
            error("entity is not type of Villager")
        }
        entity.profession = profession
        entity.villagerType = type
        entity.villagerLevel = experience.ordinal + 1
        entity.villagerExperience = experience.experience
    }

    override fun createSettingsGuiSlots(): List<GuiSlot> =
        super.createSettingsGuiSlots() + listOf(
            ShopEntityData.createEnumGuiSlot(ItemBuilder(Material.EMERALD)
                .name("${ChatColor.GREEN}職業: ${ChatColor.WHITE}${profession.name}")
                .build(), profession) { profession = it },
            ShopEntityData.createEnumGuiSlot(ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name("${ChatColor.GREEN}種類: ${ChatColor.WHITE}${type.name}")
                .hideAllFlags()
                .build(), type) { type = it },
            ShopEntityData.createEnumGuiSlot(ItemBuilder(Material.EXPERIENCE_BOTTLE)
                .name("${ChatColor.GREEN}経験値: ${ChatColor.WHITE}${experience.name}")
                .build(), experience) { experience = it },
        )
}