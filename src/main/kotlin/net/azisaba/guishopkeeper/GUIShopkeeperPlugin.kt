package net.azisaba.guishopkeeper

import net.azisaba.guishopkeeper.commands.GUIShopkeeperCommand
import net.azisaba.guishopkeeper.config.Config
import net.azisaba.guishopkeeper.gui.screens.ShopNPCSettingsScreen
import net.azisaba.guishopkeeper.gui.screens.ShopScreen
import net.azisaba.guishopkeeper.gui.screens.ShopSettingsScreen
import net.azisaba.guishopkeeper.gui.screens.ShopTradeSettingsScreen
import net.azisaba.guishopkeeper.listener.PlayerSignListener
import net.azisaba.guishopkeeper.listener.ShopListener
import net.azisaba.guishopkeeper.shop.Shopkeepers
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class GUIShopkeeperPlugin : JavaPlugin() {
    val shopkeepers = Shopkeepers(this)

    override fun onEnable() {
        Config // load config

        // register command
        Bukkit.getPluginCommand("guishopkeeper")?.setExecutor(GUIShopkeeperCommand(this))

        // register listeners
        Bukkit.getPluginManager().registerEvents(PlayerSignListener, this)
        Bukkit.getPluginManager().registerEvents(ShopListener(this), this)
        Bukkit.getPluginManager().registerEvents(ShopSettingsScreen.EventListener, this)
        Bukkit.getPluginManager().registerEvents(ShopTradeSettingsScreen.EventListener, this)
        Bukkit.getPluginManager().registerEvents(ShopNPCSettingsScreen.EventListener, this)
        Bukkit.getPluginManager().registerEvents(ShopScreen.EventListener, this)

        // respawn all NPCs
        sync(1) {
            shopkeepers.respawn()
        }
    }

    override fun onDisable() {
        Bukkit.getOnlinePlayers().forEach { player ->
            if (player.openInventory.topInventory.holder?.javaClass?.typeName?.startsWith("net.azisaba.guishopkeeper") == true) {
                player.closeInventory()
            }
        }

        // Despawn all NPCs
        shopkeepers.despawn()
    }

    fun async(delay: Long = 0, action: () -> Unit) =
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, action, delay)

    fun sync(delay: Long = 0, action: () -> Unit) =
        Bukkit.getScheduler().runTaskLater(this, action, delay)
}
