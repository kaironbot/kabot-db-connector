package org.wagham.db.scopes

import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testBuildings(
    client: KabotMultiDBClient,
    guildId: String
) {
    "getAllBuildingRecipes should be able to get all the buildings" {
        client.buildingsScope.getAllBuildingRecipes(guildId).count() shouldBeGreaterThan 0
    }

    "getBuildingsWithBounty should be able to get all the buildings" {
        client.buildingsScope.getBuildingsWithBounty(guildId).count() shouldBeGreaterThan 0
    }
}