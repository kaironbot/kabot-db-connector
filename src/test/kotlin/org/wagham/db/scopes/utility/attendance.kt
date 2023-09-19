package org.wagham.db.scopes.utility

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.AttendanceReport
import org.wagham.db.models.embed.AttendanceReportPlayer
import org.wagham.db.utils.dateAtMidnight
import org.wagham.db.uuid
import java.util.*
import kotlin.random.Random

fun KabotMultiDBClientTest.testAttendance(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Should be able of adding the current daily attendance" {
        client.utilityScope.updateAttendance(
            guildId,
            AttendanceReport(
                dateAtMidnight(Calendar.getInstance().time),
                uuid(),
                mapOf(
                    uuid() to listOf(AttendanceReportPlayer(Random.nextInt(0, 100), uuid(), uuid()))
                )
            )
        )
    }

    "Should not be able of getting the daily attendance from a non-existing guild" {
        shouldThrow<InvalidGuildException> {
            client.utilityScope.getLastAttendance(uuid())
        }
    }

    "Should be able of getting the attendance and updating it" {
        val attendance = AttendanceReport(
            dateAtMidnight(Calendar.getInstance().time),
            uuid(),
            mapOf(
                uuid() to listOf(AttendanceReportPlayer(Random.nextInt(0, 100), uuid(), uuid()))
            )
        )
        client.utilityScope.updateAttendance(guildId, attendance) shouldBe true
        val retrievedAttendance = client.utilityScope.getLastAttendance(guildId)
        retrievedAttendance.date shouldBe attendance.date
        retrievedAttendance.message shouldBe attendance.message
        retrievedAttendance.players.size shouldBe 1

        val newPlayer = uuid()
        client.utilityScope.updateAttendance(
            guildId,
            attendance.copy(players = attendance.players +
                    (newPlayer to listOf(AttendanceReportPlayer(Random.nextInt(0, 100), uuid(), uuid())))
            )
        ) shouldBe true

        val updatedAttendance = client.utilityScope.getLastAttendance(guildId)
        updatedAttendance.date shouldBe attendance.date
        updatedAttendance.message shouldBe attendance.message
        updatedAttendance.players.keys shouldContain newPlayer

    }

}