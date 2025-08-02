package org.wagham.db.models.embed

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Strike(
	@Contextual val date: Date,
	val title: String
)