package org.wagham.db.models

import kotlinx.serialization.Serializable

@Serializable
data class CharacterUpdate (
    val character: String,
    val ms: Int,
    val isAlive: Boolean,
    val reputationAdjustment: Map<String, String> = emptyMap(),
)