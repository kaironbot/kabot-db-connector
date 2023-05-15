package org.wagham.db.models.embed

interface ItemRequirement {
    val cost: Float
    val buildings: Set<String>
    val tools: Set<String>
    val reputation: Set<ReputationRequirement>
}