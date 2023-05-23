package org.wagham.db.models.embed

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.wagham.db.models.AnnouncementType
import org.wagham.db.utils.JacksonEmptyStringToNullDeserializer

data class Prize(
    val probability: Float,
    val moneyDelta: Int,
    val guaranteedItems: Map<String, Int> = emptyMap(),
    @JsonDeserialize(using = JacksonEmptyStringToNullDeserializer::class) val announceId: AnnouncementType?,
    val randomItems: List<ItemWithProbability> = listOf()
)