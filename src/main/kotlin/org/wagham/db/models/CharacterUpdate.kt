package org.wagham.db.models

import org.wagham.db.enums.CharacterStatus
import java.util.*

data class CharacterUpdate (
    val character: String,
    val ms: Int,
    val isAlive: Boolean,
    val reputationAdjustment: Map<String, String> = emptyMap(),
)