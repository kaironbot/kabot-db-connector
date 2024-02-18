package org.wagham.db.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import org.wagham.db.models.Player

class JacksonSessionResponsibleDeserializer : JsonDeserializer<Any?>() {
	override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Any? {
		val node: JsonNode = p.readValueAsTree()
		return when {
			node.isNull -> null
			node.isObject -> ctxt?.readTreeAsValue(node, Player::class.javaObjectType)
			node.isTextual -> node.asText()
			else -> null
		}
	}
}