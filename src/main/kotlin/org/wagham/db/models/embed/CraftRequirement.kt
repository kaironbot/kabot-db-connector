package org.wagham.db.models.embed

import kotlinx.serialization.Serializable

@Serializable
data class CraftRequirement(
    val timeRequired: Long? = null,
    val minQuantity: Int? = null,
    val maxQuantity: Int? = null,
    val materials: Map<String, Float> = mapOf(),
    val label: String? = null,
    override val cost: Float = 0f,
    override val buildings: Set<String> = setOf(),
    override val tools: Set<String> = setOf(),
    override val reputation: Set<ReputationRequirement> = setOf()
) : ItemRequirement