package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class Item (
    @BsonId val name: String,
    @JsonProperty("sell_price") val sellPrice: Float,
    @JsonProperty("sell_proficiencies") val sellProficiencies: List<String> = listOf(),
    @JsonProperty("buy_price") val buyPrice: Float,
    @JsonProperty("is_usable") val usable: Boolean,
    val link: String,
    val category: String,
    val manual: String,
    val attunement: Boolean,
    @JsonProperty("give_ratio") val giveRatio: Float,
    val craft: Craft
)

data class Craft(
    @JsonProperty("craft_mo_cost") val craftMoCost: Float,
    val tier: String,
    @JsonProperty("craft_tools") val craftTools: List<String> = listOf(),
    @JsonProperty("craft_tbadge") val craftTBadge: Int,
    @JsonProperty("craft_time") val craftTime: Int,
    @JsonProperty("craft_total_cost") val craftTotalCost: Float,
    @JsonProperty("craft_min_qty") val craftMinQty: Int,
    @JsonProperty("craft_max_qty") val craftMaxQty: Int,
    @JsonProperty("craft_ingredients") val ingredients: Map<String, Int> = mapOf()
)