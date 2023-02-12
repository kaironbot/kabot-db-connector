package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
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

}