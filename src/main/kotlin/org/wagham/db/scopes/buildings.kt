package org.wagham.db.scopes

import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.BuildingRecipe
import org.wagham.db.pipelines.characters.BuildingWithBounty

class KabotDBBuildingScope(
    private val client: KabotMultiDBClient
) {
    fun getAllBuildingRecipes(guildId: String) =
        client.getGuildDb(guildId).getCollection<BuildingRecipe>("buildingtypes").find("{}").toFlow()

    fun getBuildingsWithBounty(guildId: String) =
        client.getGuildDb(guildId)
            .getCollection<BuildingRecipe>("buildingtypes")
            .aggregate<BuildingWithBounty>(BuildingWithBounty.getPipeline())
            .toFlow()
}
