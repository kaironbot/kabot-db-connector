package org.wagham.db.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.utils.BinaryData

@Serializable
data class CharacterSheet(
	@BsonId val id: String,
	val token: BinaryData? = null
)