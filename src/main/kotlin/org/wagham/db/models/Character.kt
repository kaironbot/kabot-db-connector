package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.CharacterStatus
import java.util.Date

data class Character (
    @BsonId val name: String,
    val player: String,
    val race: String?,
    val territory: String?,
    @JsonProperty("class") val characterClass: String?,
    val status: CharacterStatus,
    val masterMS: Int,
    @JsonProperty("PBCMS") val pbcMS: Int,
    val errataMS: Int,
    val errata: List<Errata>,
    val created: Date?,
    val lastPlayed: Date?,
    val lastMastered: Date?,
    val reputation: Map<String, Int>,
    val buildings: Map<String, List<Building>> = mapOf(),
    val inventory: Map<String, Int> = mapOf(),
    val languages: List<String> = listOf(),
    val money: Float = 0f,
    val proficiencies: List<String> = listOf()
)