package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.utils.findPrevious

data class ExpTable (
    @BsonId val utilType: String,
    val table: Map<Int, String>
) {

    private val levelToExp = table.keys.fold(mapOf<String, Int>()) { acc, it ->
        acc + (table[it]!! to it)
    }

    fun levelToExp(level: String) = levelToExp[level]

    fun expToLevel(exp: Float) = table[table.keys.toList().findPrevious(exp)]
}