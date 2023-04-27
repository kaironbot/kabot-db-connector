package org.wagham.db.models.embed

data class AbilityScores(
    val id: String,
    val type: AbilityScoresType,
    val manualInsert: String? = null,
    val scores: List<Int>
)

enum class AbilityScoresType { ROLLED, POINT_BUY }
