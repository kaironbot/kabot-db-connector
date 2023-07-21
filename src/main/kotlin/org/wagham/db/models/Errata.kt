package org.wagham.db.models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.utils.JacksonLenientCharacterStateDeserializer
import java.util.*

data class Errata (
    val ms: Int = 0,
    val description: String = "",
    val date: Date,
    val reputationAdjustment: Map<String, String> = emptyMap(),
    @JsonDeserialize(using = JacksonLenientCharacterStateDeserializer::class) val statusChange: CharacterStatus? = null
)