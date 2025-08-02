package org.wagham.db.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.Strike
import java.util.Date

@Serializable
data class Player(
	@BsonId val playerId: String,
	val name: String,
	@Contextual val dateJoined: Date?,
	val activeCharacter: String? = null,
	@Contextual val masterSince: Date? = null,
	val strikes: List<Strike> = emptyList(),
)