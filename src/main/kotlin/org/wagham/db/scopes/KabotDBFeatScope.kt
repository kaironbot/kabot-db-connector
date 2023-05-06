package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Feat

class KabotDBFeatScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Feat> {

    override val collectionName = CollectionNames.FEATS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Feat> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllFeats(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllFeats(guildId: String, feats: List<Feat>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(feats)
            }.insertedIds.size == feats.size
}

