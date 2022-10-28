package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testUtility(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getExpTable should be able of getting the exp table" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.table.size shouldBeGreaterThan 0
    }

    "expTable should be able of getting the exp for a defined level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.table.size shouldBeGreaterThan 0
        val randomExp = expTable.table.keys.random()
        val randomLevel = expTable.table[randomExp]
        randomLevel shouldNotBe null
        expTable.levelToExp(randomLevel!!) shouldBe randomExp
    }

    "expTable should be able of getting the level for any exp value" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.table.size shouldBeGreaterThan 0

        val randomInnerIndex = (0 until expTable.table.size-1).random()
        val randomInnerExp = (expTable.table.keys.toList()[randomInnerIndex] + expTable.table.keys.toList()[randomInnerIndex+1]) / 2
        expTable.expToLevel(randomInnerExp.toFloat()) shouldBe expTable.table.values.toList()[randomInnerIndex]

        val randomOuterLower = expTable.table.keys.first() - 10
        expTable.expToLevel(randomOuterLower.toFloat()) shouldBe expTable.table.values.toList().first()

        val randomOuterUpper = expTable.table.keys.last() + 10
        expTable.expToLevel(randomOuterUpper.toFloat()) shouldBe expTable.table.values.toList().last()
    }

}