package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Bounty

class KabotDBBountyScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Bounty> {

    override val collectionName = CollectionNames.BOUNTIES.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Bounty> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllBounties(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllBounties(guildId: String, bounties: List<Bounty>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(bounties)
            }.insertedIds.size == bounties.size
}
