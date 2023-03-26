package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class PlayerBuildingsMessages(
    @BsonId val id: String,
    val player: String,
    @JsonProperty("header_message") val headerMessage: String,
    val messages: List<BuildingMessage>
) {

    override fun equals(other: Any?): Boolean =
        other != null && other is PlayerBuildingsMessages && (
            this.id == other.id &&
            this.player == other.player &&
            this.headerMessage == other.headerMessage &&
            this.messages.all {
                other.messages.contains(it)
            }

        )

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + player.hashCode()
        result = 31 * result + headerMessage.hashCode()
        result = 31 * result + messages.sumOf { 31 * it.hashCode() }
        return result
    }

}

data class BuildingMessage(
    @JsonProperty("message_id") val messageId: String,
    @JsonProperty("building_name") val buildingName: String,
    val zone: String,
    val description: String
)