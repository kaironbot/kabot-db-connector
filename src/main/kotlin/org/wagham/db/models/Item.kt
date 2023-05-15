package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.BuySellRequirement
import org.wagham.db.models.embed.CraftRequirement

data class Item (
    @BsonId val name: String,
    val sell: BuySellRequirement? = null,
    val buy: BuySellRequirement? = null,
    val usable: Boolean = false,
    val link: String? = null,
    val tier: String? = null,
    val category: String? = null,
    val manual: String? = null,
    val attunement: Boolean = false,
    val giveRatio: Float = 1f,
    val craft: CraftRequirement? = null
)