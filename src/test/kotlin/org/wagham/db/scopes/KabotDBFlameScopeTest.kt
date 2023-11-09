package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.flow.first
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.MongoCredentials
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.random.Random

class KabotDBFlameScopeTest : StringSpec() {

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
        testFlame()
    }

    private fun StringSpec.testFlame() {

        "Should be able to get all the flame sentences in a guild" {
            client.flameScope.getFlame(guildId).count() shouldBeGreaterThan 0
        }

        "Should be able to add a flame sentence" {
            val newValue = UUID.randomUUID().toString()
            client.flameScope.addFlame(guildId, newValue)
            client.flameScope.getFlame(guildId).toList().also {
                it shouldContain newValue
            }.size shouldBeGreaterThan 0
        }

        "should be able to update and get today's flame count" {
            val calendar = Calendar.getInstance()
            val startingDate = LocalDateTime.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
            val currentDate = Date.from(startingDate.toInstant(ZoneOffset.UTC))
            val count = Random.nextInt(0, 10)
            client.flameScope.addToFlameCount(guildId, count)
            client.flameScope.getFlameCount(guildId).first { it.date == currentDate }.count shouldBeGreaterThanOrEqual count
        }

        "Should not be able to get the flame for a non-existing Guild" {
            shouldThrow<InvalidGuildException> {
                client.flameScope.getFlame("I_DO_NOT_EXIST")
            }
        }

        "Should not be able to get the flame count for a non-existing Guild" {
            shouldThrow<InvalidGuildException> {
                client.flameScope.getFlameCount("I_DO_NOT_EXIST")
            }
        }

    }
}