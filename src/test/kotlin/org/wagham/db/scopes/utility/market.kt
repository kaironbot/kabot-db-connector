package org.wagham.db.scopes.utility

import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.models.WeeklyMarket
import org.wagham.db.models.embed.CraftRequirement
import org.wagham.db.utils.dateAtMidnight
import org.wagham.db.uuid
import java.util.*

fun KabotMultiDBClientTest.testMarket(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Can update a WeeklyMarket or create if one does not exists" {
        val id = dateAtMidnight(Date())
        val market = WeeklyMarket(
            id,
            mapOf(
                uuid() to CraftRequirement(0L, 1, 100)
            )
        )

        client.utilityScope.updateMarket(guildId, market) shouldBe true

        client.utilityScope.getLastMarket(guildId) shouldBe market
    }

}
