package org.wagham.db.pipelines.characters

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.Document
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.litote.kmongo.document
import org.litote.kmongo.lookup
import org.litote.kmongo.match
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.models.Building
import org.wagham.db.models.Errata
import org.wagham.db.models.Player
import java.util.*

data class CharacterWithPlayer (
    @BsonId val name: String,
    val player: List<Player> = listOf(),
    val race: String?,
    val territory: String?,
    @JsonProperty("class") val characterClass: String?,
    val status: CharacterStatus,
    val masterMS: Int,
    val PBCMS: Int,
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

class CharactersWithPlayer {
    companion object {

        fun getPipeline(status: CharacterStatus?): List<Bson> {
            val matchStep = if (status != null) listOf(match(Document(mapOf("status" to status))))
                else listOf()
            return matchStep + lookup("players", "player", "_id", "player")
        }

    }
}