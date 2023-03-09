package net.azisaba.guishopkeeper.shop

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import net.azisaba.guishopkeeper.GUIShopkeeperPlugin
import net.azisaba.guishopkeeper.config.Config
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftMob
import org.bukkit.entity.Entity
import java.io.File
import java.lang.ref.WeakReference

class Shopkeepers(private val plugin: GUIShopkeeperPlugin) {
    private val file = File("plugins/GUIShopkeeper/shops.yml")

    var config: ShopkeepersData = reload()

    fun reload() =
        file.let {
            if (!it.exists()) {
                it.parentFile.mkdirs()
                it.writeText(Yaml.default.encodeToString(ShopkeepersData()))
            }
            Yaml.default.decodeFromString(ShopkeepersData.serializer(), it.readText()).apply { config = this }
        }

    private val entities = mutableListOf<ShopkeeperEntity>()

    /**
     * Despawn and removes all entities
     */
    fun despawn() {
        // remove all entities
        entities.forEach { it.getEntity()?.remove() }
        entities.clear()
    }

    /**
     * (Re)spawn all entities
     */
    fun respawn() {
        // despawn first
        despawn()
        // spawn entities
        config.shops.forEach { data ->
            data.location.toBukkit().apply {
                if (world == null) {
                    plugin.logger.warning("ワールド${data.location.world}は読み込まれていません。")
                    return@forEach
                }
                @Suppress("UNCHECKED_CAST")
                world.spawn(this, data.entityType.entityClass as Class<Entity>) { entity ->
                    entities.add(ShopkeeperEntity(WeakReference(world), entity.uniqueId, data))
                    entity.isInvulnerable = true
                    entity.customName = "${ChatColor.GREEN}${data.name}"
                    entity.isCustomNameVisible = Config.config.customNameVisible
                    if (entity is CraftMob) {
                        entity.setAI(false)
                        entity.handle.setPersistent()
                        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.let {
                            it.baseValue = 1000.0
                            entity.health = it.baseValue
                        }
                    }
                    data.entityData.apply(entity)
                }
            }
        }
    }

    fun getShopData(entity: Entity) =
        entities.firstOrNull { it.world.get() == entity.world && it.entityId == entity.uniqueId }?.data

    fun getEntityOf(data: ShopData) =
        entities.firstOrNull { it.data == data }?.getEntity()

    fun save() {
        file.writeText(Yaml.default.encodeToString(ShopkeepersData.serializer(), config))
    }
}
