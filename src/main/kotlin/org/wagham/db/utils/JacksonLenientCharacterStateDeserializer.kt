package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.wagham.db.enums.CharacterStatus

internal class JacksonLenientCharacterStateDeserializer : JsonDeserializer<CharacterStatus>() {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext?): CharacterStatus {
        val node: JsonNode = jsonParser.readValueAsTree()
        return parseCharacterState(node.asText())
    }

    private fun parseCharacterState(value: String) =
        try {
            if (value == "traitor") CharacterStatus.retired
            else CharacterStatus.valueOf(value)
        } catch (_: Exception) { CharacterStatus.retired }
}