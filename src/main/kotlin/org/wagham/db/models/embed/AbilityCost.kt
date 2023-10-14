package org.wagham.db.models.embed

data class AbilityCost(
    val moCost: Float = 0f,
    val itemsCost:  Map<String, Int> = emptyMap(),
    val timeRequired: Long? = 0
)
