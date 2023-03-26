package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Announcement
import org.wagham.db.models.AnnouncementBatch
import org.wagham.db.models.BuildingMessage
import org.wagham.db.models.PlayerBuildingsMessages
import org.wagham.db.uuid
import java.util.UUID

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

    "Should be able of getting the announcements in a Guild" {
        client.utilityScope.getAnnouncements(guildId, "prizes").let {
            it.id shouldBe "prizes"
            it.fail.size shouldBeGreaterThan 0
            it.criticalFail.size shouldBeGreaterThan 0
            it.success.size shouldBeGreaterThan 0
            it.jackpot.size shouldBeGreaterThan 0
            it.lostBeast.size shouldBeGreaterThan 0
            it.winBeast.size shouldBeGreaterThan 0
        }
    }

    "Should not be able of getting the announcements for a non-existent Guild" {
        shouldThrow<InvalidGuildException> {
            client.utilityScope.getAnnouncements("I_DO_NOT_EXIST", "prizes")
        }
    }

    "Should not be able of getting the announcements of a non-existent batch" {
        shouldThrow<ResourceNotFoundException> {
            client.utilityScope.getAnnouncements(guildId, "I_DO_NOT_EXIST")
        }
    }

    "Should be able of updating the announcements" {
        val newContent = UUID.randomUUID().toString()
        val announcements = client.utilityScope.getAnnouncements(guildId, "prizes").let {
            it.copy(
                success = it.success + Announcement(newContent)
            )
        }

        client.utilityScope.updateAnnouncements(guildId, "prizes", announcements) shouldBe true

        val newAnnouncements = client.utilityScope.getAnnouncements(guildId, "prizes")

        newAnnouncements.id shouldBe "prizes"
        newAnnouncements.success.map { it.rawString } shouldContain newContent
    }

    "Should be able of creating a PlayerBuildMessage if does not exist" {
        val messageToCreate = PlayerBuildingsMessages(uuid(), uuid(), uuid(), emptyList())
        client.utilityScope.updateBuildingMessage(guildId, messageToCreate) shouldBe true
        client.utilityScope.getBuildingsMessages(guildId).firstOrNull {
            it.id == messageToCreate.id
        } shouldNotBe null
    }

    "Should be able of updating a PlayerBuildMessage" {
        val messageToCreate = PlayerBuildingsMessages(uuid(), uuid(), uuid(), emptyList())
        client.utilityScope.updateBuildingMessage(guildId, messageToCreate) shouldBe true
        val messageToUpdate = messageToCreate.copy(messages = listOf(
            BuildingMessage(uuid(), uuid(), uuid(), uuid())
        ))
        client.utilityScope.updateBuildingMessage(guildId, messageToUpdate) shouldBe true
        client.utilityScope.getBuildingsMessages(guildId).firstOrNull {
            it.id == messageToCreate.id
        }.let {
            it shouldNotBe null
            it!!.messages.size shouldBe 1
            it.messages.first().messageId shouldBe messageToUpdate.messages.first().messageId
        }
    }

    "Can determine if two instances of PlayerBuildinsMessages are equal" {
        val m1 = BuildingMessage(uuid(), uuid(), uuid(), uuid())
        val m2 = BuildingMessage(uuid(), uuid(), uuid(), uuid())
        val m3 = BuildingMessage(uuid(), uuid(), uuid(), uuid())

        val pm1 = PlayerBuildingsMessages(uuid(), uuid(), uuid(), listOf(m1, m2))
        val pm2 = pm1.copy(messages = listOf(m2, m1))
        val pm3 = pm1.copy(messages = listOf(m1))
        val pm4 = pm1.copy(messages = listOf(m1, m3))
        val pm5 = pm1.copy(id = uuid())

        (pm1 == pm2) shouldBe true
        (pm1 == pm3) shouldBe false
        (pm1 == pm4) shouldBe false
        (pm1 == pm5) shouldBe false

    }

}