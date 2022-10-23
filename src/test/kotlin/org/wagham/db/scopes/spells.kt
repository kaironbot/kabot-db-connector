package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testSpells(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllSpells should be able to get all the spells" {
        val spells = client.spellsScope.getAllSpells(guildId)
        spells.count() shouldBeGreaterThan 0
    }

}