package net.azisaba.guishopkeeper.gui.screens

import net.azisaba.guishopkeeper.item.ItemBuilder
import net.azisaba.guishopkeeper.shop.ShopData
import net.azisaba.guishopkeeper.shop.ShopItemAir
import net.azisaba.guishopkeeper.shop.ShopTradeDataItem
import net.azisaba.guishopkeeper.util.addLore
import net.azisaba.guishopkeeper.util.isEmpty
import net.azisaba.guishopkeeper.util.toFriendlyName
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import java.lang.Integer.min

class ShopScreen(private val shopData: ShopData, private val player: Player) : Screen() {
    override val inv = Bukkit.createInventory(this, 54, "${ChatColor.GREEN}${shopData.name}")

    var page = 0

    init {
        reset()
    }

    fun reset() {
        for (i in 0..53) {
            inv.setItem(i, null)
        }
        // background
        fillBorder(blackBackgroundItem)

        // trades
        shopData.trades.subList(27 * page, min(27 + page * 27, shopData.trades.size))
            .forEachIndexed { index, trade ->
                // exclude dummy trades
                if (trade is ShopTradeDataItem) {
                    if (trade.isPriceEmpty() && !shopData.allowFreeTrade) return@forEachIndexed
                    inv.setItem(ShopSettingsScreen.guiIndexes[index]!!, trade.result.getBukkitItem().apply {
                        itemMeta = itemMeta.apply {
                            addLore("")
                            addLore("${ChatColor.GOLD}${ChatColor.UNDERLINE}コスト")
                            trade.price.filter { it !is ShopItemAir }.forEach { item ->
                                val color = if (item.isEnough(player.inventory)) ChatColor.GREEN else ChatColor.RED
                                val stack = item.getBukkitItem()
                                addLore("${ChatColor.GRAY}- ${ChatColor.WHITE}${stack.toFriendlyName()} ${color}x${stack.amount} ${ChatColor.DARK_GRAY}(type: ${stack.type.name})")
                            }
                        }
                    })
                }
            }

        // previous page
        if (page > 0) {
            inv.setItem(45, ItemBuilder(Material.ARROW).name("${ChatColor.YELLOW}← 前のページ").build())
        }

        // next page
        if (shopData.trades.size >= 27 * (page + 1)) {
            inv.setItem(53, ItemBuilder(Material.ARROW).name("${ChatColor.YELLOW}次のページ →").build())
        }

        // close
        inv.setItem(49, ItemBuilder(Material.BARRIER).name("${ChatColor.RED}閉じる").build())
    }

    object EventListener : Listener {
        @EventHandler
        fun onClick(e: InventoryClickEvent) {
            if (e.inventory.holder !is ShopScreen) return
            if (e.clickedInventory?.holder !is ShopScreen) return
            e.isCancelled = true
            val screen = e.inventory.holder as ShopScreen
            if (e.slot in 10..43) {
                if (e.currentItem.isEmpty()) return
                val index = ShopSettingsScreen.reverseGuiIndexes[e.slot] ?: return
                val trade = screen.shopData.trades.getOrNull(index + screen.page * 27) as? ShopTradeDataItem ?: return
                if (screen.player.inventory.firstEmpty() == -1) {
                    screen.player.closeInventory()
                    screen.player.sendMessage("${ChatColor.RED}インベントリがいっぱいです。")
                    return
                }
                if (trade.price.all { it.isEnough(screen.player.inventory) }) {
                    trade.price.forEach { it.take(screen.player.inventory) }
                    // player should have enough space to take the item
                    screen.player.inventory.addItem(trade.result.getBukkitItem())
                } else {
                    screen.player.sendMessage("${ChatColor.RED}アイテムが足りません。")
                }
            }
            when (e.slot) {
                45 -> {
                    if (e.currentItem?.type == Material.ARROW) {
                        screen.page--
                        screen.reset()
                    }
                }
                53 -> {
                    if (e.currentItem?.type == Material.ARROW) {
                        screen.page++
                        screen.reset()
                    }
                }
                49 -> {
                    screen.player.closeInventory()
                }
            }
        }
    }
}
