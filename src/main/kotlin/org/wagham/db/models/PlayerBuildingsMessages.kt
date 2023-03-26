package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class PlayerBuildingsMessages(
    @BsonId val id: String,
    val player: String,
    @JsonProperty("header_message") val headerMessage: String,
    val messages: List<BuildingMessage>
)

data class BuildingMessage(
    @JsonProperty("message_id") val messageId: String,
    @JsonProperty("building_name") val buildingName: String,
    val zone: String,
    val description: String
)