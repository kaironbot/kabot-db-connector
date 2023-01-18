package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException

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
        val randomLevel = expTable.expToLevel(randomExp.toFloat())
        expTable.levelToExp(randomLevel) shouldBe randomExp
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

    "expTable should be able of getting the exp for a level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.table.size shouldBeGreaterThan 0
        val randomExp = expTable.table.keys.random()
        val randomLevel = expTable.expToLevel(randomExp.toFloat())
        expTable.levelToExp(randomLevel) shouldBe randomExp
    }

    "expTable should not be able of getting the exp for a non-existent level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.table.size shouldBeGreaterThan 0
        shouldThrow<IllegalArgumentException> {
            expTable.levelToExp("I_DO_NOT_EXIST")
        }
    }

    "getExpTable should be able of getting the tier table" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.tier.size shouldBeGreaterThan 0
    }

    "expTable should be able of getting the tier for a defined level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.tier.size shouldBeGreaterThan 0
        val randomExp = expTable.tier.keys.random()
        val randomTier = expTable.expToTier(randomExp.toFloat())
        expTable.tierToExp(randomTier) shouldBe randomExp
    }

    "expTable should be able of getting the tier for any exp value" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.tier.size shouldBeGreaterThan 0

        val randomInnerIndex = (0 until expTable.tier.size-1).random()
        val randomInnerExp = (expTable.tier.keys.toList()[randomInnerIndex] + expTable.tier.keys.toList()[randomInnerIndex+1]) / 2
        expTable.expToTier(randomInnerExp.toFloat()) shouldBe expTable.tier.values.toList()[randomInnerIndex]

        val randomOuterLower = expTable.tier.keys.first() - 10
        expTable.expToTier(randomOuterLower.toFloat()) shouldBe expTable.tier.values.toList().first()

        val randomOuterUpper = expTable.tier.keys.last() + 10
        expTable.expToTier(randomOuterUpper.toFloat()) shouldBe expTable.tier.values.toList().last()
    }

    "expTable should be able of getting the tier for a level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.tier.size shouldBeGreaterThan 0
        val randomExp = expTable.tier.keys.random()
        val randomTier = expTable.expToTier(randomExp.toFloat())
        expTable.tierToExp(randomTier) shouldBe randomExp
    }

    "expTable should not be able of getting the tier for a non-existent level" {
        val expTable = client.utilityScope.getExpTable(guildId)
        expTable.tier.size shouldBeGreaterThan 0
        shouldThrow<IllegalArgumentException> {
            expTable.tierToExp("I_DO_NOT_EXIST")
        }
    }

    "Should be able of getting the proficiencies in a Guild" {
        client.utilityScope.getProficiencies(guildId).size shouldBeGreaterThan 0
    }

    "Should not be able of getting the proficiencies for a non-existent Guild" {
        shouldThrow<InvalidGuildException> {
            client.utilityScope.getProficiencies("I_DO_NOT_EXIST")
        }
    }

}