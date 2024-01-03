package org.wagham.db.models.embed

import kotlinx.serialization.Serializable

@Serializable
data class ReputationRequirement(
    val territory: String,
    val minValue: Int
)