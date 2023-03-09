package net.azisaba.guishopkeeper.shop

import org.bukkit.World
import org.bukkit.entity.Entity
import java.lang.ref.WeakReference
import java.util.UUID

data class ShopkeeperEntity(val world: WeakReference<World>, val entityId: UUID, val data: ShopData) {
    fun getEntity(): Entity? = world.get()?.getEntity(entityId)
}
