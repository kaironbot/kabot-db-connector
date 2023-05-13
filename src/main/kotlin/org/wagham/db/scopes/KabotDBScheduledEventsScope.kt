package org.wagham.db.scopes

import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineCollection
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.ScheduledEvent

class KabotDBScheduledEventsScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<ScheduledEvent> {

    override val collectionName = CollectionNames.SCHEDULED_EVENTS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<ScheduledEvent> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllScheduledEvents(guildId: String) =
        getMainCollection(guildId).find(BsonDocument()).toFlow()

    suspend fun addScheduledEvent(guildId: String, event: ScheduledEvent) =
        getMainCollection(guildId).insertOne(event).wasAcknowledged()

}