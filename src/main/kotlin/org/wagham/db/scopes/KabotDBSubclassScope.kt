package org.wagham.db.scopes

import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.Subclass

class KabotDBSubclassScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Subclass> {

    override val collectionName = CollectionNames.CLASSES.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Subclass> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllSubclasses(guildId: String) =
        getMainCollection(guildId).find("{}").toFlow()

    suspend fun rewriteAllSubclasses(guildId: String, classes: List<Subclass>) =
        getMainCollection(guildId)
            .let {
                it.deleteMany("{}")
                it.insertMany(classes)
            }.insertedIds.size == classes.size

}

