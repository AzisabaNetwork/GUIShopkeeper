package net.azisaba.guishopkeeper.shop

import kotlinx.serialization.Serializable

@Serializable
data class ShopkeepersData(
    val shops: MutableList<ShopData> = mutableListOf(),
)
