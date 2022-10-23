package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testFeats(
    client: KabotMultiDBClient,
    guildId: String
) {
    "getAllFeats should be able to get all the feats" {
        val feats = client.featsScope.getAllFeats(guildId)
        feats.count() shouldBeGreaterThan 0
    }
}