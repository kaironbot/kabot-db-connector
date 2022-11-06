package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class BuildingRecipe (
    @BsonId val name: String,
    @JsonProperty("mo_cost") val moCost: Int,
    @JsonProperty("tbadge_cost") val tbadgeCost: Int,
    @JsonProperty("tbadge_type") val tbadgeType: String,
    @JsonProperty("proficiency_reduction") val proficiencyReduction: String?,
    @JsonProperty("bounty_id") val bountyId: String,
    val size: String,
    val areas: List<String> = listOf(),
    @JsonProperty("desc_size") val maxDescriptionSize: Int = 300
)