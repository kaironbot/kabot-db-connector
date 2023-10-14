package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.wagham.db.models.embed.CraftRequirement

class JacksonLenientCraftDeserializer : JsonDeserializer<List<CraftRequirement>>() {

    override fun deserialize(jsonParser: JsonParser, ctx: DeserializationContext?): List<CraftRequirement> {
        val node: JsonNode = jsonParser.readValueAsTree()
        return when {
            node.isPojo -> listOfNotNull(ctx?.readTreeAsValue(node, CraftRequirement::class.javaObjectType))
            node.isArray -> {
                val buffer = mutableListOf<CraftRequirement>()
                var i = 0
                while(node.has(i)) {
                    ctx?.readTreeAsValue(node.get(i), CraftRequirement::class.javaObjectType)?.also {
                        buffer.add(it)
                    }
                    i += 1
                }
                return buffer
            }
            else -> emptyList()
        }
    }

}