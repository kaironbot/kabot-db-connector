package org.wagham.db.pipelines.sessions

import com.fasterxml.jackson.annotation.JsonProperty
import com.mongodb.client.model.UnwindOptions
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.wagham.db.models.Character
import org.wagham.db.models.CharacterUpdate
import org.wagham.db.models.GameDate
import org.wagham.db.models.embed.LabelStub
import java.util.*

data class PlayerMasteredSessions(
    @BsonId val id: ObjectId,
    val master: String,
    val date: Date,
    val title: String,
    val duration: Int,
    val characters: List<CharacterUpdate>,
    val uid: Int,
    @JsonProperty("game_date") val gameDate: GameDate,
    val masterCharacter: Character,
    val unwindCounter: Int,
    val labels: Set<LabelStub> = setOf()
) {

    companion object {

        fun getPipeline(player: String): List<Bson> =
            listOf(
                lookup(
                    from = "characters",
                    localField = "master",
                    foreignField = "_id",
                    newAs = "masterCharacter"
                ),
                unwind("\$masterCharacter", UnwindOptions()
                    .includeArrayIndex("unwindCounter")
                    .preserveNullAndEmptyArrays(false)),
                match(
                    PlayerMasteredSessions::masterCharacter / Character::player eq player,
                    PlayerMasteredSessions::unwindCounter eq 0
                )
            )

    }

}