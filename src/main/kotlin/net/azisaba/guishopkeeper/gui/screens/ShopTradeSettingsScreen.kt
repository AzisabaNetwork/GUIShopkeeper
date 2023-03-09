package net.azisaba.guishopkeeper.gui.screens

import io.lumine.xikage.mythicmobs.MythicMobs
import net.azisaba.guishopkeeper.config.Config
import net.azisaba.guishopkeeper.item.ItemBuilder
import net.azisaba.guishopkeeper.listener.PlayerSignListener.promptSign
import net.azisaba.guishopkeeper.shop.ShopItemAir
import net.azisaba.guishopkeeper.shop.ShopItemMythic
import net.azisaba.guishopkeeper.shop.ShopItemVanilla
import net.azisaba.guishopkeeper.shop.ShopTradeDataItem
import net.azisaba.guishopkeeper.util.addLore
import net.azisaba.guishopkeeper.util.isEmpty
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import kotlin.math.min

class ShopTradeSettingsScreen(
    private val settingsScreen: ShopSettingsScreen,
    private val trade: ShopTradeDataItem,
) : Screen() {
    override val inv = Bukkit.createInventory(this, 54, "取引設定")

    init {
        reset()
    }

    fun reset() {
        for (i in 0..53) {
            inv.setItem(i, null)
        }
        // background
        fillBorder(blackBackgroundItem)

        // trade result
        inv.setItem(4, trade.result.getBukkitItem().apply {
            itemMeta = itemMeta?.apply {
                addLore("")
                val what = if (trade.result is ShopItemVanilla) "MMアイテム" else "バニラアイテム"
                addLore("${ChatColor.YELLOW}☄ 右クリックで${what}に変換")
            }
        })

        // minus
        inv.setItem(3, ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
            .name("${ChatColor.RED}アイテムを減らす")
            .customModelData(Config.config.customModelDataMinus)
            .build())

        // plus
        inv.setItem(5, ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
            .name("${ChatColor.GREEN}アイテムを増やす")
            .customModelData(Config.config.customModelDataPlus)
            .build())

        // price
        trade.price.subList(0, min(27, trade.price.size)).forEachIndexed { index, price ->
            // exclude dummy trades
            if (price !is ShopItemAir) {
                inv.setItem(ShopSettingsScreen.guiIndexes[index]!!, price.getBukkitItem().apply {
                    itemMeta = itemMeta.apply {
                        addLore("")
                        val what = if (price is ShopItemVanilla) "MMアイテム" else "バニラアイテム"
                        addLore("${ChatColor.YELLOW}☄ 右クリックで${what}に変換")
                    }
                })
            }
        }

        // back button
        inv.setItem(49, ItemBuilder(Material.ARROW).name("${ChatColor.GRAY}前の画面に戻る").build())
    }

    object EventListener : Listener {
        @EventHandler
        fun onClick(e: InventoryClickEvent) {
            // Ignore if unrelated inventory is opened
            if (e.inventory.holder !is ShopTradeSettingsScreen) return
            if (e.clickedInventory?.holder !is ShopTradeSettingsScreen) return
            e.isCancelled = true
            val screen = e.inventory.holder as ShopTradeSettingsScreen
            val trade = screen.trade
            val settingsScreen = screen.settingsScreen
            if (e.slot in 10..43) {
                // edit price
                val index = ShopSettingsScreen.reverseGuiIndexes[e.slot] ?: return
                if (!e.cursor.isEmpty() && e.currentItem.isEmpty()) {
                    screen.trade.setPriceAt(index, ShopItemVanilla.fromBukkitItem(e.cursor!!))
                    screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                    screen.reset()
                    return
                }
                if (index >= screen.trade.price.size) return
                val price = screen.trade.price[index]
                if (price is ShopItemAir) return
                if (price.accepts(e.cursor) && e.click == ClickType.RIGHT) {
                    screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                    if (price is ShopItemVanilla) price.amount++
                    if (price is ShopItemMythic) price.amount++
                    screen.reset()
                    return
                }
                if (e.click != ClickType.RIGHT) return
                if (price is ShopItemVanilla) {
                    ShopItemMythic.fromBukkitItem(price.getBukkitItem()).let {
                        if (it == null) {
                            settingsScreen.player.promptSign { lines ->
                                val mythicType = lines.joinToString("")
                                val mythicItem = MythicMobs.inst().itemManager.getItemStack(mythicType)
                                if (mythicItem == null) {
                                    settingsScreen.player.sendMessage("${ChatColor.RED}Mythic Item ${mythicType}は存在しません。")
                                    return@promptSign
                                }
                                trade.setPriceAt(index, ShopItemMythic(mythicType, price.amount))
                                screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                                screen.reset()
                                screen.settingsScreen.plugin.sync {
                                    screen.settingsScreen.player.openInventory(screen.inv)
                                }
                            }
                        } else {
                            trade.setPriceAt(index, it)
                        }
                    }
                } else {
                    trade.setPriceAt(index, ShopItemVanilla.fromBukkitItem(price.getBukkitItem()))
                }
                screen.reset()
            }
            when (e.slot) {
                3 -> {
                    if (trade.result is ShopItemVanilla) trade.result.amount = maxOf(1, trade.result.amount - 1)
                    if (trade.result is ShopItemMythic) trade.result.amount = maxOf(1, trade.result.amount - 1)
                    screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                    screen.reset()
                }
                5 -> {
                    if (trade.result is ShopItemVanilla) trade.result.amount = minOf(64, trade.result.amount + 1)
                    if (trade.result is ShopItemMythic) trade.result.amount = minOf(64, trade.result.amount + 1)
                    screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                    screen.reset()
                }
                4 -> {
                    if (e.isRightClick) {
                        val result = trade.result
                        if (result is ShopItemVanilla) {
                            ShopItemMythic.fromBukkitItem(trade.result.getBukkitItem()).let {
                                if (it == null) {
                                    settingsScreen.player.promptSign { lines ->
                                        val mythicType = lines.joinToString("")
                                        val mythicItem = MythicMobs.inst().itemManager.getItemStack(mythicType)
                                        if (mythicItem == null) {
                                            settingsScreen.player.sendMessage("${ChatColor.RED}${mythicType}は存在しません。")
                                            return@promptSign
                                        }
                                        trade.result = ShopItemMythic(mythicType, result.amount)
                                        screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                                        screen.reset()
                                        screen.settingsScreen.plugin.sync {
                                            screen.settingsScreen.player.openInventory(screen.inv)
                                        }
                                    }
                                } else {
                                    trade.result = it
                                }
                            }
                        } else {
                            trade.result = ShopItemVanilla.fromBukkitItem(result.getBukkitItem())
                        }
                        screen.reset()
                    }
                }
                49 -> {
                    screen.settingsScreen.plugin.sync {
                        screen.settingsScreen.player.openInventory(settingsScreen.inv)
                    }
                }
            }
        }
    }
}