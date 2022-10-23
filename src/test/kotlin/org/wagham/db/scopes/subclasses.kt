package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testSubclasses(
    client: KabotMultiDBClient,
    guildId: String
) {

    "getAllSubclasses should be able to get all the subclasses" {
        val subclasses = client.subclassesScope.getAllSubclasses(guildId)
        subclasses.count() shouldBeGreaterThan 0
    }

}