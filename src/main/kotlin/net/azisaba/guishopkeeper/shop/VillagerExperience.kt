package net.azisaba.guishopkeeper.shop

import org.bukkit.Material

/**
 * Villager experience
 */
enum class VillagerExperience(val material: Material, val experience: Int) {
    Novice(Material.COBBLESTONE, 0),
    Apprentice(Material.IRON_BLOCK, 10),
    Journeyman(Material.GOLD_BLOCK, 70),
    Expert(Material.EMERALD_BLOCK, 150),
    Master(Material.DIAMOND_BLOCK, 250),
}
