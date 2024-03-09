package org.wagham.db.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object StringNormalizer {

	private val latinMap = this::class.java.getResource("latin_map.json")?.readText()?.let {
		jacksonObjectMapper().readValue<Map<Char, String>>(it)
	} ?: throw IllegalStateException("Cannot load latin map for normalizer")

	fun normalize(input: String): String {
		return input.lowercase().map { char ->
			latinMap[char] ?: char.toString()
		}.joinToString("").replace(Regex("[^a-z0-9]"), "")
	}

}