package org.wagham.db.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SessionOutcome(
    val characterId: String,
    val exp: Int,
    val isDead: Boolean
)
