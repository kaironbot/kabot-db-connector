package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testPlayers(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllPlayers should be able to get all the players" {
        val players = client.playersScope.getAllPlayers(guildId)
        players.count() shouldBeGreaterThan 0
    }

}