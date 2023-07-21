package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.exceptions.InvalidGuildException

fun KabotMultiDBClientTest.testPlayers(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllPlayers should be able to get all the players" {
        val players = client.playersScope.getAllPlayers(guildId)
        players.count() shouldBeGreaterThan 0
    }

    "Cannot get players from a non existent guild" {
        shouldThrow<InvalidGuildException> {
            client.playersScope.getAllPlayers("I_DO_NOT_EXIST")
        }
    }

}