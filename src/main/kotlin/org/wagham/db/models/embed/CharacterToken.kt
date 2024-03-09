package org.wagham.db.models.embed

import kotlinx.serialization.Serializable
import org.wagham.db.utils.Base64String

@Serializable
data class CharacterToken(
	val image: Base64String,
	val mimeType: String
)