package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.utils.dateAtMidnight
import org.wagham.db.utils.daysInBetween
import java.time.DayOfWeek
import java.util.*

fun KabotMultiDBClientTest.testSessions(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllSessions should be able to get all the sessions" {
        client.sessionScope.getAllSessions(guildId).count() shouldBeGreaterThan 0
    }

    "getAllSessions should be able to get all the sessions after a certain date" {
        val startingDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
        client.sessionScope
            .getAllSessions(guildId, startDate = startingDate)
            .onEach {
                it.date shouldBeGreaterThanOrEqualTo startingDate
            }.count() shouldBeGreaterThan 0
    }

    "getAllSessions should be able to get all the sessions before a certain date" {
        val endDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
        client.sessionScope
            .getAllSessions(guildId, endDate = endDate)
            .onEach {
                it.date shouldBeLessThanOrEqualTo endDate
            }.count() shouldBeGreaterThan 0
    }

    "getAllSessions should be able to get all the sessions between two dates" {
        val startingDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
        val endDate = client.sessionScope.getAllSessions(guildId).filter {
            it.date > startingDate
        }.take(1000).toList().random().date
        client.sessionScope
            .getAllSessions(guildId, startDate = startingDate, endDate = endDate)
            .onEach {
                it.date shouldBeGreaterThanOrEqualTo startingDate
                it.date shouldBeLessThanOrEqualTo endDate
            }.count() shouldBeGreaterThan 0
    }

    "Cannot get sessions from a non existent guild" {
        shouldThrow<InvalidGuildException> {
            client.sessionScope.getAllSessions("I_DO_NOT_EXIST")
        }
    }

    "Can get all the sessions for a player" {
        val master = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().master
        val masterPlayer = client.charactersScope.getCharacter(guildId, master).player
        client.sessionScope.getAllMasteredSessions(guildId, masterPlayer)
            .onEach {
                client.charactersScope.getCharacter(guildId, it.master).player shouldBe masterPlayer
                it.masterCharacter.player shouldBe masterPlayer
            }.count() shouldBeGreaterThan 0
    }

    "Can calculate the days passed in-game between two dates" {
        val calendar = Calendar.getInstance()
        val startDate = client.sessionScope.getAllSessions(guildId).take(1000).toList().random().date
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_WEEK, +7)
        val endDate = calendar.time

        val normalizedStartDate = dateAtMidnight(startDate)
        val normalizedEndDate = dateAtMidnight(endDate)
        val inGameDays = client.sessionScope
            .getAllSessions(guildId)
            .filter {
                it.date in normalizedStartDate..normalizedEndDate
            }.toList()
            .groupBy { it.date }
            .map { sessions -> sessions.value.maxOf { it.duration } }
            .sum() + 1L + daysInBetween(normalizedStartDate, normalizedEndDate)

        client.sessionScope.getTimePassedInGame(guildId, startDate, endDate) shouldBe inGameDays
    }

}