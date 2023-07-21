package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.uuid

fun KabotMultiDBClientTest.testRaces(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllRaces should be able to get all the races" {
        client.raceScope.getAllRaces(guildId).count() shouldBeGreaterThan 0
    }

    "Should not be able of getting races from a non-existent guild" {
        shouldThrow<InvalidGuildException> {
            client.raceScope.getAllRaces(uuid())
        }
    }

    "Should be able of rewriting the whole race collection" {
        val races = client.raceScope.getAllRaces(guildId).toList()
        val raceToEdit = races.random().copy(
            race = uuid(),
            link = uuid(),
        )
        client.raceScope.rewriteAllRaces(
            guildId,
            races.filter { it.id != raceToEdit.id } + raceToEdit
        ) shouldBe true
        val newRaces = client.raceScope.getAllRaces(guildId).toList()
        newRaces.size shouldBe races.size
        newRaces.first { it.id == raceToEdit.id }.let {
            it.race shouldBe raceToEdit.race
            it.link shouldBe raceToEdit.link
        }
    }

    "Should not be able of updating the races for a non-existent guild" {
        val races = client.raceScope.getAllRaces(guildId).toList()
        shouldThrow<InvalidGuildException> {
            client.raceScope.rewriteAllRaces(
                uuid(),
                races
            )
        }
    }

}