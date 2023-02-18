package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.Background
import org.wagham.db.models.Race

class KabotDBRaceScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Race> {

    override val collectionName = "races"

    override fun getMainCollection(guildId: String): CoroutineCollection<Race> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllRaces(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllRaces(guildId: String, races: List<Race>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(races)
            }.insertedIds.size == races.size
}