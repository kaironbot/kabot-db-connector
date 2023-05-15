package org.wagham.db.models.embed

data class BuySellRequirement (
    override val cost: Float = 0f,
    override val buildings: Set<String> = emptySet(),
    override val tools: Set<String> = emptySet(),
    override val reputation: Set<ReputationRequirement> = emptySet()
) : ItemRequirement