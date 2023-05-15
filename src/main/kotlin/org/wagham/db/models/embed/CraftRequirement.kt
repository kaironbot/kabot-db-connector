package org.wagham.db.models.embed

data class CraftRequirement(
    val timeRequired: Long?,
    val minQuantity: Int?,
    val maxQuantity: Int?,
    val materials: Map<String, Int> = mapOf(),
    override val cost: Float = 0f,
    override val buildings: Set<String> = setOf(),
    override val tools: Set<String> = setOf(),
    override val reputation: Set<ReputationRequirement> = setOf()
) : ItemRequirement