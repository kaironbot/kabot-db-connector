package org.wagham.db.models.creation

data class CharacterCreationData(
    val name: String,
    val startingLevel: String,
    val race: String?,
    val characterClass: String?,
    val territory: String?,
    val age: Int?
)
