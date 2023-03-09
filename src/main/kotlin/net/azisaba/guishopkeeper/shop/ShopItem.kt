package net.azisaba.guishopkeeper.shop

import io.lumine.xikage.mythicmobs.MythicMobs
import kotlinx.serialization.Serializable
import net.azisaba.guishopkeeper.util.asCraftMirror
import net.azisaba.guishopkeeper.util.asNMSCopy
import net.minecraft.server.v1_15_R1.MojangsonParser
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

@Serializable
sealed interface ShopItem {
    var amount: Int

    fun getBukkitItem(): ItemStack

    fun accepts(itemStack: ItemStack?): Boolean

    fun isEnough(playerInventory: PlayerInventory): Boolean {
        var amount = this.amount
        for (i in 0..35) {
            val invItem = playerInventory.getItem(i) ?: continue
            if (accepts(invItem)) {
                amount -= invItem.amount
            }
        }
        return amount <= 0
    }

    fun take(playerInventory: PlayerInventory) {
        var amount = this.amount
        for (i in 0..35) {
            val invItem = playerInventory.getItem(i) ?: continue
            if (accepts(invItem)) {
                if (invItem.amount > amount) {
                    invItem.amount -= amount
                    amount = 0
                } else {
                    amount -= invItem.amount
                    playerInventory.setItem(i, null)
                }
            }
            if (amount <= 0) {
                break
            }
        }
    }
}

@Serializable
object ShopItemAir : ShopItem {
    override var amount = 0

    override fun getBukkitItem(): ItemStack = ItemStack(Material.AIR)

    override fun accepts(itemStack: ItemStack?) = true

    override fun isEnough(playerInventory: PlayerInventory): Boolean = true

    override fun take(playerInventory: PlayerInventory) {}
}

@Serializable
data class ShopItemVanilla(
    val type: Material,
    override var amount: Int,
    val tag: String? = null,
) : ShopItem {
    companion object {
        fun fromBukkitItem(item: ItemStack): ShopItemVanilla {
            val tag = item.asNMSCopy().tag?.toString()
            return ShopItemVanilla(item.type, item.amount, tag)
        }
    }

    override fun getBukkitItem(): ItemStack {
        val item = ItemStack(type, amount)
        if (tag != null) {
            return item.asNMSCopy().apply { this.tag = MojangsonParser.parse(this@ShopItemVanilla.tag) }.asCraftMirror()
        }
        return item
    }

    override fun accepts(itemStack: ItemStack?): Boolean = getBukkitItem().isSimilar(itemStack)
}

@Serializable
data class ShopItemMythic(
    val type: String,
    override var amount: Int,
) : ShopItem {
    companion object {
        fun fromBukkitItem(item: ItemStack): ShopItemMythic? {
            val tag = item.asNMSCopy().tag?.getString("MYTHIC_TYPE") ?: return null
            if (tag.isBlank()) return null
            return ShopItemMythic(tag, item.amount)
        }
    }

    override fun getBukkitItem(): ItemStack =
        MythicMobs.inst()
            .itemManager
            .getItemStack(type)
            ?.asNMSCopy()
            ?.asCraftMirror()
            ?.apply { this.amount = this@ShopItemMythic.amount }
            ?: error("mythic item $type is not loaded")

    override fun accepts(itemStack: ItemStack?): Boolean =
        itemStack?.asNMSCopy()?.tag?.getString("MYTHIC_TYPE") == type || getBukkitItem().isSimilar(itemStack)
}
