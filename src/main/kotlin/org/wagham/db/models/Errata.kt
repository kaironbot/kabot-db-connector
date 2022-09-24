package org.wagham.db.models

import org.wagham.db.enums.CharacterStatus
import java.util.*

data class Errata (
    val ms: Int = 0,
    val description: String = "",
    val date: Date,
    val reputationAdjustment: Map<String, String>,
    val statusChange: CharacterStatus?
)