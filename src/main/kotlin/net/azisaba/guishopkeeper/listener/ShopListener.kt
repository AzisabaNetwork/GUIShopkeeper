package net.azisaba.guishopkeeper.listener

import net.azisaba.guishopkeeper.GUIShopkeeperPlugin
import net.azisaba.guishopkeeper.gui.screens.ShopScreen
import net.azisaba.guishopkeeper.gui.screens.ShopSettingsScreen
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class ShopListener(private val plugin: GUIShopkeeperPlugin) : Listener {
    @EventHandler
    fun onShopClick(e: PlayerInteractEntityEvent) {
        val shopData = plugin.shopkeepers.getShopData(e.rightClicked) ?: return
        e.isCancelled = true
        if (e.player.hasPermission("guishopkeeper.settings") && e.player.isSneaking) {
            e.player.openInventory(ShopSettingsScreen(plugin, shopData, e.player).inventory)
            return
        }
        if (!e.player.hasPermission("guishopkeeper.use")) {
            return
        }
        e.player.openInventory(ShopScreen(shopData, e.player).inventory)
    }

    @EventHandler
    fun onShopClickArmorStand(e: PlayerInteractAtEntityEvent) {
        if (e.rightClicked.type != EntityType.ARMOR_STAND) return
        onShopClick(e)
    }
}
