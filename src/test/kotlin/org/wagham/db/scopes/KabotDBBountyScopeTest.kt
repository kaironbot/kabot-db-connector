package org.wagham.db.scopes

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.AnnouncementType
import org.wagham.db.models.MongoCredentials
import org.wagham.db.models.embed.Prize
import org.wagham.db.uuid
import java.util.*
import kotlin.random.Random

class KabotDBBountyScopeTest : StringSpec() {

    private val client = KabotMultiDBClient(
        MongoCredentials(
            "ADMIN",
            System.getenv("DB_TEST_USER").shouldNotBeNull(),
            System.getenv("DB_TEST_PWD").shouldNotBeNull(),
            System.getenv("TEST_DB").shouldNotBeNull(),
            System.getenv("DB_TEST_IP").shouldNotBeNull(),
            System.getenv("DB_TEST_PORT").shouldNotBeNull().toInt(),
        )
    )
    private val guildId = System.getenv("TEST_DB_ID").shouldNotBeNull()

    init {
        testBounties()
    }

    private fun StringSpec.testBounties() {

        "getAllBounties should be able to get all the bounties" {
            client.bountiesScope.getAllBounties(guildId).count() shouldBeGreaterThan 0
        }

        "getAllBounties should not be able of getting the backgrounds for a non-existent guild" {
            shouldThrow<InvalidGuildException> {
                client.bountiesScope.getAllBounties("I_DO_NOT_EXIST")
            }
        }

        "Should be able of rewriting the whole bounties collection" {
            val bounties = client.bountiesScope.getAllBounties(guildId).toList()
            val bountyToEdit = bounties.random().copy(
                prizes = emptyList()
            )
            client.bountiesScope.rewriteAllBounties(
                guildId,
                bounties.filter { it.id != bountyToEdit.id } + bountyToEdit
            ) shouldBe true
            val newBounties = client.bountiesScope.getAllBounties(guildId).toList()
            newBounties.size shouldBe bounties.size
            newBounties.first { it.id == bountyToEdit.id }.prizes.size shouldBe 0
        }

        "Should not be able of updating the bounties for a non-existent guild" {
            val bounties = client.bountiesScope.getAllBounties(guildId).toList()
            shouldThrow<InvalidGuildException> {
                client.bountiesScope.rewriteAllBounties(
                    UUID.randomUUID().toString(),
                    bounties
                )
            }
        }

        "Should be able of serializing and deserializing prizes" {
            val objectMapper = ObjectMapper().registerKotlinModule()
            val prizeWithAnnouncement = Prize(
                Random.nextFloat(),
                Random.nextInt(),
                mapOf(uuid() to Random.nextInt()),
                listOf(AnnouncementType.CriticalFail, AnnouncementType.Success, AnnouncementType.Fail, AnnouncementType.Jackpot).random()
            )
            val prizeWithAnnouncementString = objectMapper.writeValueAsString(prizeWithAnnouncement)
            objectMapper.readValue<Prize>(prizeWithAnnouncementString).let {
                it.probability shouldBe prizeWithAnnouncement.probability
                it.moneyDelta shouldBe prizeWithAnnouncement.moneyDelta
                it.guaranteedItems shouldBe prizeWithAnnouncement.guaranteedItems
                it.announceId shouldBe prizeWithAnnouncement.announceId
            }

            val prizeWithNullAnnouncement = Prize(
                Random.nextFloat(),
                Random.nextInt(),
                mapOf(uuid() to Random.nextInt()),
                null
            )
            val prizeWithNullAnnouncementString = objectMapper.writeValueAsString(prizeWithNullAnnouncement)
            objectMapper.readValue<Prize>(prizeWithNullAnnouncementString).let {
                it.probability shouldBe prizeWithNullAnnouncement.probability
                it.moneyDelta shouldBe prizeWithNullAnnouncement.moneyDelta
                it.guaranteedItems shouldBe prizeWithNullAnnouncement.guaranteedItems
                it.announceId shouldBe null
            }

            val prizeWithEmptyAnnouncement = Prize(
                Random.nextFloat(),
                Random.nextInt(),
                mapOf(uuid() to Random.nextInt()),
                null
            )
            val prizeWithEmptyAnnouncementString = objectMapper.writeValueAsString(prizeWithEmptyAnnouncement)
                .replace("null", "\"\"")
            objectMapper.readValue<Prize>(prizeWithEmptyAnnouncementString).let {
                it.probability shouldBe prizeWithEmptyAnnouncement.probability
                it.moneyDelta shouldBe prizeWithEmptyAnnouncement.moneyDelta
                it.guaranteedItems shouldBe prizeWithEmptyAnnouncement.guaranteedItems
                it.announceId shouldBe null
            }
        }
    }

}