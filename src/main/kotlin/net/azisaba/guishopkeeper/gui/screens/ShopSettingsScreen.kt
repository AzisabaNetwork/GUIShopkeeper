package net.azisaba.guishopkeeper.gui.screens

import net.azisaba.guishopkeeper.GUIShopkeeperPlugin
import net.azisaba.guishopkeeper.item.ItemBuilder
import net.azisaba.guishopkeeper.shop.ShopData
import net.azisaba.guishopkeeper.shop.ShopItemVanilla
import net.azisaba.guishopkeeper.shop.ShopTradeDataEmpty
import net.azisaba.guishopkeeper.shop.ShopTradeDataItem
import net.azisaba.guishopkeeper.util.addLore
import net.azisaba.guishopkeeper.util.isEmpty
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.min

class ShopSettingsScreen(val plugin: GUIShopkeeperPlugin, val shop: ShopData, val player: Player) : Screen() {
    override val inv = Bukkit.createInventory(this, 54, "Shop取引設定")

    private var page = 0

    init {
        reset()
    }

    fun reset() {
        fillBorder(blackBackgroundItem)

        // trades
        shop.trades.subList(27 * page, min(27 + page * 27, shop.trades.size)).forEachIndexed { index, trade ->
            // exclude dummy trades
            if (trade is ShopTradeDataItem) {
                inv.setItem(guiIndexes[index]!!, trade.result.getBukkitItem().apply {
                    itemMeta = itemMeta.apply {
                        addLore("")
                        addLore("§e✎ 左クリックで取引を編集")
                        addLore("§e✖ Shift+右クリックで取引を削除")
                    }
                })
            }
        }

        // previous page
        if (page > 0) {
            inv.setItem(45, ItemBuilder(Material.ARROW).name("§e← 前のページ").build())
        }

        // next page
        if (shop.trades.size >= 27 * (page + 1)) {
            inv.setItem(53, ItemBuilder(Material.ARROW).name("§e次のページ →").build())
        }

        // npc settings button
        inv.setItem(49, ItemBuilder(Material.COMPARATOR).name("§eNPC設定").build())
    }

    object EventListener : Listener {
        @EventHandler
        fun onClick(e: InventoryClickEvent) {
            // Ignore if unrelated inventory is opened
            if (e.inventory.holder !is ShopSettingsScreen) return
            if (e.clickedInventory?.holder !is ShopSettingsScreen) return
            e.isCancelled = true
            val screen = e.inventory.holder as ShopSettingsScreen
            if (e.slot in 10..43) {
                val index = reverseGuiIndexes[e.slot] ?: return
                if (!e.cursor.isEmpty() && e.currentItem.isEmpty()) {
                    screen.shop.setTradeAt(index, ShopTradeDataItem(ShopItemVanilla.fromBukkitItem(e.cursor!!)))
                    screen.plugin.async { screen.plugin.shopkeepers.save() }
                    screen.reset()
                    return
                }
                if (index >= screen.shop.trades.size) return
                if (e.isLeftClick) {
                    // edit trade
                    val trade = screen.shop.trades[index]
                    if (trade is ShopTradeDataItem) {
                        screen.plugin.sync {
                            screen.player.openInventory(ShopTradeSettingsScreen(screen, trade).inv)
                        }
                    }
                } else if (e.click == ClickType.SHIFT_RIGHT) {
                    // remove trade
                    screen.shop.trades[index] = ShopTradeDataEmpty
                    screen.inv.setItem(e.slot, null)
                    screen.plugin.async { screen.plugin.shopkeepers.save() }
                    screen.reset()
                }
            }
            // previous page
            if (e.slot == 45 && e.currentItem?.type == Material.ARROW) {
                screen.page--
                screen.reset()
            }
            // next page
            if (e.slot == 53 && e.currentItem?.type == Material.ARROW) {
                screen.page++
                screen.reset()
            }
            if (e.slot == 49) {
                screen.plugin.sync {
                    screen.player.openInventory(ShopNPCSettingsScreen(screen).inv)
                }
            }
        }
    }

    companion object {
        val guiIndexes = mapOf(
            0 to 10, 1 to 11, 2 to 12, 3 to 13, 4 to 14, 5 to 15, 6 to 16,
            7 to 19, 8 to 20, 9 to 21, 10 to 22, 11 to 23, 12 to 24, 13 to 25,
            14 to 28, 15 to 29, 16 to 30, 17 to 31, 18 to 32, 19 to 33, 20 to 34,
            21 to 37, 22 to 38, 23 to 39, 24 to 40, 25 to 41, 26 to 42, 27 to 43,
        )

        val reverseGuiIndexes = guiIndexes.map { it.value to it.key }.toMap()
    }
}
