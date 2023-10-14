package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.models.embed.ProficiencyStub
import org.wagham.db.utils.JacksonLenientCharacterStateDeserializer
import org.wagham.db.utils.JacksonLenientDndClassDeserializer
import java.util.Date

data class Character (
    @BsonId val id: String,
    val name: String,
    val player: String,
    val race: String?,
    val territory: String?,
    @JsonProperty("class") @JsonDeserialize(using = JacksonLenientDndClassDeserializer::class) val characterClass: List<String> = emptyList(),
    @JsonDeserialize(using = JacksonLenientCharacterStateDeserializer::class) val status: CharacterStatus = CharacterStatus.active,
    val masterMS: Int = 0,
    @JsonProperty("PBCMS") val pbcMS: Int = 0,
    val errataMS: Int = 0,
    val sessionMS: Int = 0,
    val errata: List<Errata> = emptyList(),
    val created: Date? = null,
    val lastPlayed: Date? = null,
    val lastMastered: Date? = null,
    val age: Int? = null,
    val reputation: Map<String, Int>? = emptyMap(),
    val buildings: Map<String, List<Building>> = emptyMap(),
    val inventory: Map<String, Int> = emptyMap(),
    val languages: Set<ProficiencyStub> = emptySet(),
    val money: Float = 0f,
    val proficiencies: Set<ProficiencyStub> = emptySet(),
    val labels: Set<LabelStub> = emptySet()
) {

    fun ms() = listOf(masterMS, pbcMS, errataMS, sessionMS).sum()

}