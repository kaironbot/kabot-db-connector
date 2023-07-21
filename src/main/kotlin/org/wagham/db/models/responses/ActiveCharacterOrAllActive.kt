package org.wagham.db.models.responses

import org.wagham.db.models.Character

data class ActiveCharacterOrAllActive(
    val currentActive: Character? = null,
    val allActive: List<Character> = emptyList()
)
