package org.wagham.db.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.utils.JacksonLenientCharacterStateDeserializer
import java.util.*

@Serializable
data class Errata (
    val ms: Int = 0,
    val description: String = "",
    @Contextual val date: Date,
    val reputationAdjustment: Map<String, String> = emptyMap(),
    @JsonDeserialize(using = JacksonLenientCharacterStateDeserializer::class) val statusChange: CharacterStatus? = null
)