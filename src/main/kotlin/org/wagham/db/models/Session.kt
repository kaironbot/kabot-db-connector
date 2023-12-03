package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.utils.JacksonLenientObjectIdDeserializer
import java.util.Date

data class GenericSession<R>(
    @JsonDeserialize(using = JacksonLenientObjectIdDeserializer::class) @BsonId val id: String,
    val master: String,
    val date: Date,
    val title: String,
    val duration: Int,
    val characters: List<CharacterUpdate>,
    val uid: Int,
    @JsonProperty("game_date") val gameDate: GameDate? = null,
    val labels: Set<LabelStub> = setOf(),
    val registeredBy: R? = null
)

typealias Session = GenericSession<String>