package org.wagham.db.scopes

import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.enums.ScheduledEventState
import org.wagham.db.models.ScheduledEvent
import org.wagham.db.utils.isSuccessful

class KabotDBScheduledEventsScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<ScheduledEvent> {

    override val collectionName = CollectionNames.SCHEDULED_EVENTS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<ScheduledEvent> =
        client.getGuildDb(guildId).getCollection(collectionName)

    fun getAllScheduledEvents(guildId: String, state: ScheduledEventState? = null) =
        getMainCollection(guildId).find(
            state?.let {
                ScheduledEvent::state eq it
            } ?: BsonDocument()
        ).toFlow()

    suspend fun addScheduledEvent(guildId: String, event: ScheduledEvent) =
        getMainCollection(guildId).insertOne(event).wasAcknowledged()

    suspend fun updateState(guildId: String, eventId: String, state: ScheduledEventState) =
        getMainCollection(guildId)
            .updateOne(
                ScheduledEvent::id eq eventId,
                set(ScheduledEvent::state setTo state)
            ).isSuccessful()

}