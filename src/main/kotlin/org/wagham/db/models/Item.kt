package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class ReputationRequirement(
    val territory: String,
    @JsonProperty("minvalue") val minValue: Int
)

data class Item (
    @BsonId @JsonProperty("_id") val name: String,
    @JsonProperty("sell_price") val sellPrice: Float,
    @JsonProperty("sell_proficiencies") val sellProficiencies: List<String> = listOf(),
    @JsonProperty("sell_building_requirement") val sellBuildingRequirement: String?,
    @JsonProperty("buy_price") val buyPrice: Float,
    @JsonProperty("is_usable") val usable: Boolean,
    val link: String,
    val category: String,
    val manual: String,
    val attunement: Boolean,
    @JsonProperty("give_ratio") val giveRatio: Float,
    @JsonProperty("buy_rep_requirement") val buyReputationRequirement: ReputationRequirement?,
    val craft: Craft
)

data class Craft(
    @JsonProperty("craft_mo_cost") val craftMoCost: Float?,
    val tier: String?,
    @JsonProperty("craft_tools") val craftTools: List<String> = listOf(),
    @JsonProperty("craft_tbadge") val craftTBadge: Int?,
    @JsonProperty("craft_time") val craftTime: Int?,
    @JsonProperty("craft_total_cost") val craftTotalCost: Float?,
    @JsonProperty("craft_min_qty") val craftMinQty: Int?,
    @JsonProperty("craft_max_qty") val craftMaxQty: Int?,
    @JsonProperty("craft_rep_requirement") val craftReputationRequirement: ReputationRequirement?,
    @JsonProperty("building_required") val buildingRequired: String?,
    @JsonProperty("craft_ingredients") val ingredients: Map<String, Int> = mapOf()
)