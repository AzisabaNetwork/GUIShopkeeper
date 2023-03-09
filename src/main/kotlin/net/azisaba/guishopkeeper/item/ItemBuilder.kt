package net.azisaba.guishopkeeper.item

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ItemBuilder(private val type: Material) {
    private var amount = 1
    private var name: String? = null
    private var lore: List<String>? = null
    private var hideFlags: Set<ItemFlag> = emptySet()
    private var customModelData: Int? = null

    fun amount(amount: Int) = apply { this.amount = amount }
    fun name(name: String) = apply { this.name = name }
    fun lore(lore: List<String>) = apply { this.lore = lore }
    fun lore(vararg lore: String) = apply { this.lore = lore.toList() }
    fun hideAllFlags() = apply { hideFlags(*ItemFlag.values()) }
    fun hideFlags(vararg flags: ItemFlag) = apply { this.hideFlags = flags.toSet() }
    fun customModelData(data: Int?) = apply { this.customModelData = data }

    fun build() =
        ItemStack(type, amount).apply {
            itemMeta?.let {
                if (this@ItemBuilder.name != null) {
                    it.setDisplayName(this@ItemBuilder.name)
                }
                if (this@ItemBuilder.lore != null) {
                    it.lore = this@ItemBuilder.lore
                }
                it.addItemFlags(*this@ItemBuilder.hideFlags.toTypedArray())
                if (customModelData != null) {
                    it.setCustomModelData(customModelData)
                }
                itemMeta = it
            }
        }
}
