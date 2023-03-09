package net.azisaba.guishopkeeper.util

import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location

@Serializable
data class SerializableLocation(
    val world: String?,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float,
    val pitch: Float
) {
    constructor(world: String?, x: Double, y: Double, z: Double) : this(world, x, y, z, 0f, 0f)
    constructor(world: String?, x: Int, y: Int, z: Int) : this(world, x.toDouble(), y.toDouble(), z.toDouble())
    constructor(world: String?, x: Int, y: Int, z: Int, yaw: Float, pitch: Float) : this(world, x.toDouble(), y.toDouble(), z.toDouble(), yaw, pitch)
    constructor(location: Location) : this(location.world?.name, location.x, location.y, location.z, location.yaw, location.pitch)

    fun toBukkit() = Location(world?.let { Bukkit.getWorld(it) }, x, y, z, yaw, pitch)
}
