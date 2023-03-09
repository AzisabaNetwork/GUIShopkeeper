package net.azisaba.guishopkeeper.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GuiSlot(val itemStack: ItemStack, val action: InventoryClickEvent.() -> Unit)
