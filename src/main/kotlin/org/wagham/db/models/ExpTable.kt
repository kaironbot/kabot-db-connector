package org.wagham.db.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.utils.findPrevious
import java.util.*

@Serializable
data class ExpTable (
	@BsonId val utilType: String,
	val table: SortedMap<Int, String>,
	val tier: Map<Int, String> = emptyMap()
) {

	private val levelToExp = table.keys.fold(mapOf<String, Int>()) { acc, it ->
		acc + (table[it]!! to it)
	}
	private val tierToExp = tier.keys.fold(mapOf<String, Int>()) { acc, it ->
		acc + (tier[it]!! to it)
	}
	fun levelToExp(level: String) = levelToExp[level] ?: throw IllegalArgumentException("This level does not exist")
	fun tierToExp(tier: String) = tierToExp[tier] ?: throw IllegalArgumentException("This tier does not exist")
	fun expToLevel(exp: Float) = table[table.keys.toList().findPrevious(exp)]
		?: throw IllegalArgumentException("No level for provided exp")
	fun expToTier(exp: Float) = tier[tier.keys.toList().findPrevious(exp)]
		?: throw IllegalArgumentException("No tier for provided exp")
}