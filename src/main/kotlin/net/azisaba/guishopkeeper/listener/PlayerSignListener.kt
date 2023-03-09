package net.azisaba.guishopkeeper.listener

import net.azisaba.packetapi.api.event.AsyncPlayerPreSignChangeEvent
import net.minecraft.server.v1_15_R1.BlockPosition
import net.minecraft.server.v1_15_R1.PacketPlayOutOpenSignEditor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerSignListener : Listener {
    private val awaitingSign = ConcurrentHashMap<UUID, (List<String>) -> Unit>()

    fun Player.promptSign(action: (List<String>) -> Unit) {
        val loc0 = location.clone().apply { y = 0.0 }
        val origBlockData = loc0.block.blockData
        sendBlockChange(loc0, Material.AIR.createBlockData())
        sendBlockChange(loc0, Material.OAK_SIGN.createBlockData())
        awaitingSign[uniqueId] = {
            sendBlockChange(loc0, origBlockData)
            action(it)
        }
        (this as CraftPlayer).handle.playerConnection
            .sendPacket(PacketPlayOutOpenSignEditor(BlockPosition(loc0.blockX, loc0.blockY, loc0.blockZ)))
    }

    @EventHandler
    fun onSignChange(e: AsyncPlayerPreSignChangeEvent) {
        val action = awaitingSign.remove(e.player.uniqueId) ?: return
        action(e.lines)
    }
}
