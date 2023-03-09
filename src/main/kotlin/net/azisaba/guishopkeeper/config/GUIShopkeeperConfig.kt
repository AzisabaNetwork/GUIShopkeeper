package net.azisaba.guishopkeeper.config

import com.charleskorn.kaml.YamlComment
import kotlinx.serialization.Serializable

@Serializable
data class GUIShopkeeperConfig(
    @YamlComment(
        "Whether to enable CustomNameVisible",
        "If true, the shopkeeper's name will be displayed without looking at it.",
    )
    val customNameVisible: Boolean = false,
    val customModelDataMinus: Int? = null,
    val customModelDataPlus: Int? = null,
)
