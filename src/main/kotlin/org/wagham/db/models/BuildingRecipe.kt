package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class BuildingRecipe (
    @BsonId override val name: String,
    override val type: String,
    override val tier: String,
    @JsonProperty("mo_cost") override val moCost: Int,
    override val materials: Map<String, Int> = emptyMap(),
    override val upgradeId: String? = null,
    override val upgradeOnly: Boolean = false,
    @JsonProperty("proficiency_reduction") override val proficiencyReduction: String?,
    @JsonProperty("bounty_id") override val bountyId: String,
    override val size: String,
    override val areas: List<String> = listOf(),
    @JsonProperty("desc_size") override val maxDescriptionSize: Int = 300
) : BaseBuilding