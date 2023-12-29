package org.wagham.db.models

import kotlinx.serialization.Serializable

@Serializable
data class GameDate(
    val year: Int,
    val month: String,
    val day: Int,
    val season: String
)