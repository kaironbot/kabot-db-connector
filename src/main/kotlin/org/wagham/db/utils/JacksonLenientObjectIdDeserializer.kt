package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.bson.types.ObjectId

class JacksonLenientObjectIdDeserializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): String {
        val node: JsonNode = p.readValueAsTree()
        return when {
            node.isPojo -> ctxt?.readTreeAsValue(node, ObjectId::class.javaObjectType)?.toString() ?: throw IllegalStateException("Wrong Id format")
            node.isTextual -> node.asText()
            else -> throw IllegalStateException("Wrong Id format")
        }
    }
}