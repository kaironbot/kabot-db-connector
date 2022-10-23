package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testBackgrounds(
    client: KabotMultiDBClient,
    guildId: String
) {
    "getAllBackgrounds should be able to get all the backgrounds" {
        val backgrounds = client.backgroundsScope.getAllBackgrounds(guildId)
        backgrounds.count() shouldBeGreaterThan 0
    }
}