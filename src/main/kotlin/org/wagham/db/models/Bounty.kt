package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class ItemWithProbability(
    @JsonProperty("item_id") val itemId: String,
    val qty: Int,
    @JsonProperty("p") val probability: Float
)

data class Prize(
    @JsonProperty("p") val probability: Float,
    @JsonProperty("mo_delta") val moDelta: Int,
    @JsonProperty("prize_id") val guaranteedObjectId: String?,
    @JsonProperty("prize_delta") val guaranteedObjectDelta: Int,
    @JsonProperty("announce_id") val announceId: String?,
    @JsonProperty("prize_list") val prizeList: List<ItemWithProbability> = listOf()
)

data class Bounty (
    @BsonId val id: String,
    val prizes: List<Prize>
)
