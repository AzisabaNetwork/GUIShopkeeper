package net.azisaba.guishopkeeper.util

import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack.asNMSCopy(): net.minecraft.server.v1_15_R1.ItemStack = CraftItemStack.asNMSCopy(this)

fun net.minecraft.server.v1_15_R1.ItemStack.asCraftMirror(): ItemStack = CraftItemStack.asCraftMirror(this)

fun ItemMeta.addLore(line: String) {
    lore = (lore ?: mutableListOf()).apply { add(line) }
}

fun ItemStack?.isEmpty() = this == null || type.isAir

fun ItemStack.toFriendlyName(): String {
    val meta = itemMeta ?: return (i18NDisplayName ?: type.name)
    return if (meta.hasDisplayName()) {
        meta.displayName
    } else {
        type.name
    }
}
