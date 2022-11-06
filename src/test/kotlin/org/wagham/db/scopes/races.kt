package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testRaces(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllRaces should be able to get all the races" {
        client.raceScope.getAllRaces(guildId).count() shouldBeGreaterThan 0
    }

}