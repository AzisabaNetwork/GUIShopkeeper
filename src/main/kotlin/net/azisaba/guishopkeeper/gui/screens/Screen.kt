package net.azisaba.guishopkeeper.gui.screens

import net.azisaba.guishopkeeper.item.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

abstract class Screen : InventoryHolder {
    abstract val inv: Inventory

    override fun getInventory(): Inventory = inv

    fun fillBorder(itemStack: ItemStack) {
        if (inv.size < 27) error("Inventory size must be 27 or more")
        // background
        for (i in 0..8) {
            inv.setItem(i, itemStack)
        }
        inv.setItem(9, itemStack)
        inv.setItem(17, itemStack)
        if (inv.size == 27) {
            for (i in 18..26) {
                inv.setItem(i, itemStack)
            }
            return
        }
        inv.setItem(18, itemStack)
        inv.setItem(26, itemStack)
        if (inv.size == 36) {
            for (i in 27..35) {
                inv.setItem(i, itemStack)
            }
            return
        }
        inv.setItem(27, itemStack)
        inv.setItem(35, itemStack)
        if (inv.size == 45) {
            for (i in 36..44) {
                inv.setItem(i, itemStack)
            }
            return
        }
        inv.setItem(36, itemStack)
        inv.setItem(44, itemStack)
        for (i in 45..53) {
            inv.setItem(i, itemStack)
        }
    }

    companion object {
        val blackBackgroundItem = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build()
    }
}
