package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.wagham.db.models.embed.LabelStub
import java.util.Date

data class Session(
    @BsonId val id: ObjectId,
    val master: String,
    val date: Date,
    val title: String,
    val duration: Int,
    val characters: List<CharacterUpdate>,
    val uid: Int,
    @JsonProperty("game_date") val gameDate: GameDate? = null,
    val labels: Set<LabelStub> = setOf()
)