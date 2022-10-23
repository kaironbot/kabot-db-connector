package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testItems(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllItems should be able to get all the items" {
        val items = client.itemsScope.getAllItems(guildId)
        items.count() shouldBeGreaterThan 0
    }

}