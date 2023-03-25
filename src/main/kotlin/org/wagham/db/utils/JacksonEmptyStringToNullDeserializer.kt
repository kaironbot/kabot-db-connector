package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.wagham.db.models.AnnouncementType

internal class JacksonEmptyStringToNullDeserializer : JsonDeserializer<AnnouncementType?>() {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext?): AnnouncementType? {
        val node: JsonNode = jsonParser.readValueAsTree()
        return node.asText()
            .takeIf {!it.isNullOrEmpty() }
            ?.let { parseAnnouncementType(it) }
    }

    private fun parseAnnouncementType(value: String) =
        try {
            AnnouncementType.valueOf(value)
        } catch (_: Exception) { null }
}