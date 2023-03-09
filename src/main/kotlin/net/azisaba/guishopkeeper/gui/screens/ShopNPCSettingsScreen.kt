package net.azisaba.guishopkeeper.gui.screens

import net.azisaba.guishopkeeper.gui.GuiSlot
import net.azisaba.guishopkeeper.item.ItemBuilder
import net.azisaba.guishopkeeper.listener.PlayerSignListener.promptSign
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ShopNPCSettingsScreen(
    private val settingsScreen: ShopSettingsScreen,
) : Screen() {
    override val inv = Bukkit.createInventory(this, 36, "NPC設定")
    val optionSlots = mutableMapOf<Int, GuiSlot>()

    init {
        reset()
    }

    fun reset() {
        // back
        inv.setItem(0, ItemBuilder(Material.ARROW).name("${ChatColor.YELLOW}← 戻る").build())

        // rename
        inv.setItem(1, ItemBuilder(Material.NAME_TAG)
            .name("${ChatColor.GOLD}名前を変更する")
            .lore("${ChatColor.YELLOW}現在の名前: ${ChatColor.GREEN}${settingsScreen.shop.name}")
            .build())

        // free trade
        inv.setItem(2, ItemBuilder(Material.CHEST)
            .name("${ChatColor.GOLD}コストが空の場合に無料で取引できるようにする")
            .lore("${ChatColor.YELLOW}現在の設定: ${ChatColor.GREEN}${settingsScreen.shop.allowFreeTrade}")
            .build())

        settingsScreen.shop.entityData.createSettingsGuiSlots().forEachIndexed { index, slot ->
            inv.setItem(index + 9, slot.itemStack)
            optionSlots[index + 9] = slot
        }

        // delete
        inv.setItem(35, ItemBuilder(Material.BONE)
            .name("${ChatColor.RED}NPCを削除する")
            .lore("${ChatColor.YELLOW}⚠ 取引データなどのこのNPCに関連するデータは全て削除されます")
            .build())
    }

    object EventListener : Listener {
        @EventHandler
        fun onClick(e: InventoryClickEvent) {
            // Ignore if unrelated inventory is opened
            if (e.inventory.holder !is ShopNPCSettingsScreen) return
            if (e.clickedInventory?.holder !is ShopNPCSettingsScreen) return
            e.isCancelled = true
            val screen = e.inventory.holder as ShopNPCSettingsScreen
            val settingsScreen = screen.settingsScreen
            screen.optionSlots[e.slot]?.let { slot ->
                slot.action(e)
                screen.settingsScreen.plugin.shopkeepers.getEntityOf(settingsScreen.shop)?.let {
                    settingsScreen.shop.entityData.apply(it)
                    it.teleport(settingsScreen.shop.location.toBukkit())
                }
                screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                screen.reset()
                screen.settingsScreen.plugin.sync {
                    if (screen.settingsScreen.player.openInventory.topInventory.holder !is ShopNPCSettingsScreen) {
                        screen.settingsScreen.player.openInventory(screen.inv)
                    }
                }
                return
            }
            when (e.slot) {
                0 -> {
                    screen.settingsScreen.plugin.sync {
                        screen.settingsScreen.player.openInventory(settingsScreen.inv)
                    }
                }
                1 -> {
                    screen.settingsScreen.player.promptSign {
                        screen.settingsScreen.plugin.sync {
                            screen.settingsScreen.shop.name = it.joinToString("")
                            screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                            screen.reset()
                            screen.settingsScreen.player.openInventory(screen.inv)
                        }
                    }
                }
                2 -> {
                    screen.settingsScreen.shop.allowFreeTrade = !screen.settingsScreen.shop.allowFreeTrade
                    screen.settingsScreen.plugin.async { screen.settingsScreen.plugin.shopkeepers.save() }
                    screen.reset()
                }
                35 -> {
                    screen.settingsScreen.plugin.shopkeepers.config.shops.removeIf { it.id == screen.settingsScreen.shop.id }
                    screen.settingsScreen.plugin.shopkeepers.respawn()
                    screen.settingsScreen.plugin.async {
                        screen.settingsScreen.plugin.shopkeepers.save()
                        screen.settingsScreen.plugin.sync {
                            screen.settingsScreen.player.closeInventory()
                        }
                    }
                }
            }
        }
    }
}
