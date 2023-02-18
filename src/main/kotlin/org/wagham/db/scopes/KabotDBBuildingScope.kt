package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.BuildingRecipe
import org.wagham.db.pipelines.characters.BuildingWithBounty

class KabotDBBuildingScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<BuildingRecipe> {

    override val collectionName = "buildingtypes"

    override fun getMainCollection(guildId: String): CoroutineCollection<BuildingRecipe> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllBuildingRecipes(guildId: String) =
       getMainCollection(guildId).find("{}").toFlow()

    fun getBuildingsWithBounty(guildId: String) =
        getMainCollection(guildId)
            .aggregate<BuildingWithBounty>(BuildingWithBounty.getPipeline())
            .toFlow()

    suspend fun updateBuilding(guildId: String, building: BuildingRecipe)
        = updateBuildings(guildId, listOf(building))

    suspend fun updateBuildings(guildId: String, buildings: List<BuildingRecipe>) =
        getMainCollection(guildId).let { collection ->
            client.transaction(guildId) {
                val count = buildings.map { b ->
                    val result = collection.updateOne(
                        BuildingRecipe::name eq b.name,
                        b,
                        UpdateOptions().upsert(true)
                    )
                    result.modifiedCount + (1.takeIf{ result.upsertedId != null } ?: 0)
                }.sum().toInt()
                count == buildings.size
            }.committed
        }

    suspend fun deleteBuildings(guildId: String, buildingsId: List<String>) =
        getMainCollection(guildId).deleteMany(
            or(buildingsId.map { BuildingRecipe::name eq it })
        ).deletedCount == buildingsId.size.toLong()

}
