package org.wagham.db.models

import org.wagham.db.utils.findPrevious

data class ExpTable (
    val levelToExp: Map<Int, Int>,
    val tierMap: Map<Int, Int>?
) {

    private val expToLevelMap = levelToExp.keys.fold(mapOf<Int, Int>()) { acc, it ->
        acc + (levelToExp[it]!! to it)
    }

    fun getLevelsExp() = levelToExp.values.toList()

    fun expToLevel(exp: Float) = expToLevelMap[expToLevelMap.keys.toList().findPrevious(exp)]
}