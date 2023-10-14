package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType

internal class JacksonLenientDndClassDeserializer : JsonDeserializer<List<String>>() {

    override fun deserialize(jsonParser: JsonParser, context: DeserializationContext?): List<String> {
        val node: JsonNode = jsonParser.readValueAsTree()
        return when {
            node.isTextual -> listOf(node.asText())
            node.isArray -> {
                val buffer = mutableListOf<String>()
                var i = 0
                while(node.has(i)) {
                    buffer.add(node.get(i).asText())
                    i += 1
                }
                return buffer
            }
            else -> emptyList()
        }
    }

}