package org.wagham.db.pipelines.characters

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.client.model.UnwindOptions
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.litote.kmongo.lookup
import org.litote.kmongo.match
import org.litote.kmongo.project
import org.litote.kmongo.unwind
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.Building
import org.wagham.db.models.Errata
import org.wagham.db.models.Player
import org.wagham.db.models.embed.AbilityScores
import java.util.*

data class CharacterWithPlayer (
    @BsonId val id: String,
    val name: String,
    val player: Player,
    val race: String?,
    val territory: String?,
    @JsonProperty("class") val characterClass: String?,
    val status: CharacterStatus,
    val masterMS: Int,
    @JsonProperty("PBCMS") val pbcMS: Int,
    val errataMS: Int,
    val sessionMS: Int,
    val errata: List<Errata>,
    val created: Date?,
    val lastPlayed: Date?,
    val lastMastered: Date?,
    val reputation: Map<String, Int>,
    val buildings: Map<String, List<Building>> = mapOf(),
    val inventory: Map<String, Int> = mapOf(),
    val languages: List<String> = listOf(),
    val money: Float = 0f,
    val proficiencies: List<String> = listOf(),
    val abilityScores: AbilityScores? = null,
    val abilityScoresChoice: List<AbilityScores> = emptyList()
) {
    companion object {

        fun getPipeline(status: CharacterStatus?): List<Bson> {
            val matchStep = if (status != null) listOf(match(Document(mapOf("status" to status))))
            else listOf()
            return matchStep +
                    lookup("players", "player", "_id", "player") +
                    unwind("\$player", UnwindOptions()
                        .includeArrayIndex("unwindCounter")
                        .preserveNullAndEmptyArrays(false)) +
                    match(Document(mapOf("unwindCounter" to 0))) +
                    project(Document(mapOf("unwindCounter" to 0)))

        }

    }
}
