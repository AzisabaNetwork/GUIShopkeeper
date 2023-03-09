package net.azisaba.guishopkeeper.commands

import net.azisaba.guishopkeeper.GUIShopkeeperPlugin
import net.azisaba.guishopkeeper.gui.screens.ShopSettingsScreen
import net.azisaba.guishopkeeper.shop.ShopData
import net.azisaba.guishopkeeper.util.SerializableLocation
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.Collections

class GUIShopkeeperCommand(private val plugin: GUIShopkeeperPlugin) : TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as? Player ?: return true
        if (args.isEmpty()) return true
        val cmd = args[0]
        if (!sender.hasPermission("guishopkeeper.command.guishopkeeper.$cmd")) {
            sender.sendMessage("${ChatColor.RED}このコマンドを実行する権限がありません。")
            return true
        }
        when (cmd) {
            "create" -> {
                if (args.size < 2) {
                    sender.sendMessage("${ChatColor.RED}/guishopkeeper create <entity type>")
                    return true
                }
                val entityType = runCatching { EntityType.valueOf(args[1].uppercase()) }.getOrNull() ?: run {
                    sender.sendMessage("${ChatColor.RED}エンティティの種類が無効です。")
                    return true
                }
                plugin.shopkeepers.config.shops.add(ShopData("Shopkeeper", SerializableLocation(player.location), entityType))
                plugin.shopkeepers.respawn()
                plugin.async {
                    plugin.shopkeepers.save()
                }
                sender.sendMessage("${ChatColor.GREEN}ショップを作成しました。")
            }
            "respawn" -> {
                plugin.shopkeepers.respawn()
            }
            "find" -> {
                val radius = args.getOrNull(1)?.toIntOrNull() ?: 10
                val shops = plugin.shopkeepers.config.shops.filter { it.location.toBukkit().distance(player.location) <= radius }
                sender.sendMessage("${ChatColor.GOLD}半径${radius}ブロック以内にあるShop:")
                shops.forEach {
                    sender.sendMessage("${ChatColor.GRAY}- ${ChatColor.GREEN}${it.name} ${ChatColor.GRAY}(${it.entityType.name}) (ID: ${it.id})")
                }
            }
            "settings" -> {
                val id = args.getOrNull(1) ?: run {
                    sender.sendMessage("${ChatColor.RED}/guishopkeeper settings <id>")
                    return true
                }
                val shop = plugin.shopkeepers.config.shops.find { it.id == id } ?: run {
                    sender.sendMessage("${ChatColor.RED}そのIDのShopは存在しません。")
                    return true
                }
                player.openInventory(ShopSettingsScreen(plugin, shop, player).inventory)
            }
            "movehere" -> {
                val id = args.getOrNull(1) ?: run {
                    sender.sendMessage("${ChatColor.RED}/guishopkeeper tphere <id>")
                    return true
                }
                val shop = plugin.shopkeepers.config.shops.find { it.id == id } ?: run {
                    sender.sendMessage("${ChatColor.RED}そのIDのShopは存在しません。")
                    return true
                }
                shop.location = SerializableLocation(player.location)
                plugin.shopkeepers.getEntityOf(shop)?.teleport(player.location)
                sender.sendMessage("${ChatColor.GREEN}NPCをあなたにテレポートさせました。")
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (args.size == 1) {
            return listOf("create", "respawn", "find", "settings", "movehere")
                .filter { sender.hasPermission("guishopkeeper.command.guishopkeeper.$it") }
                .filter(args[0])
        }
        if (args.size == 2) {
            if (args[0] == "create" && sender.hasPermission("guishopkeeper.command.guishopkeeper.create")) {
                return EntityType.values().map { it.name.lowercase() }.filter(args[1])
            }
            if ((args[0] == "settings" && sender.hasPermission("guishopkeeper.command.guishopkeeper.settings")) ||
                (args[0] == "movehere" && sender.hasPermission("guishopkeeper.command.guishopkeeper.movehere"))) {
                return plugin.shopkeepers.config.shops.map { it.id }.filter(args[1])
            }
        }
        return Collections.emptyList()
    }

    private fun Iterable<String>.filter(s: String) = this.filter { it.lowercase().startsWith(s.lowercase()) }
}