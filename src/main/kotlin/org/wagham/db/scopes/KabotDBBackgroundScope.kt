package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Background

class KabotDBBackgroundScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Background> {

    override val collectionName = CollectionNames.BACKGROUNDS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Background> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllBackgrounds(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllBackgrounds(guildId: String, backgrounds: List<Background>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(backgrounds)
            }.insertedIds.size == backgrounds.size
}

