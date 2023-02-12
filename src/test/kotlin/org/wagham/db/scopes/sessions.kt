package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.*
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException

fun KabotMultiDBClientTest.testSessions(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllSessions should be able to get all the sessions" {
        client.sessionScope.getAllSessions(guildId).count() shouldBeGreaterThan 0
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

}