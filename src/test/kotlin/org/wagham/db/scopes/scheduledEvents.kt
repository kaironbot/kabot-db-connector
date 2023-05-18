package org.wagham.db.scopes

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest
import org.wagham.db.enums.ScheduledEventState
import org.wagham.db.enums.ScheduledEventType
import org.wagham.db.exceptions.InvalidGuildException
import org.wagham.db.models.ScheduledEvent
import org.wagham.db.uuid
import java.util.*

fun KabotMultiDBClientTest.testScheduledEvents(
    client: KabotMultiDBClient,
    guildId: String
) {

    "Should not be able of getting scheduled events from a non-existent guild" {
        shouldThrow<InvalidGuildException> {
            client.scheduledEventsScope.getAllScheduledEvents(uuid())
        }
    }

    "Should be able of adding scheduled events and retrieving them" {
        val newEvent = ScheduledEvent(
            uuid(),
            ScheduledEventType.GIVE_ITEM,
            Date(System.currentTimeMillis()),
            Date(System.currentTimeMillis()),
            ScheduledEventState.SCHEDULED,
            args = emptyMap()
        )
        client.scheduledEventsScope.addScheduledEvent(guildId, newEvent) shouldBe true
        val events = client.scheduledEventsScope.getAllScheduledEvents(guildId).toList()
        events.size shouldBeGreaterThan 0
        events shouldContain newEvent
    }

    "Should be able to update the state of a scheduled event" {
        val newEvent = ScheduledEvent(
            uuid(),
            ScheduledEventType.GIVE_ITEM,
            Date(System.currentTimeMillis()),
            Date(System.currentTimeMillis()),
            ScheduledEventState.SCHEDULED,
            args = emptyMap()
        )
        client.scheduledEventsScope.addScheduledEvent(guildId, newEvent) shouldBe true
        client.scheduledEventsScope.updateState(guildId, newEvent.id, ScheduledEventState.COMPLETED) shouldBe true
        val events = client.scheduledEventsScope.getAllScheduledEvents(guildId, ScheduledEventState.COMPLETED).onEach {
            it.state shouldBe ScheduledEventState.COMPLETED
        }.toList()
        events.size shouldBeGreaterThan 0
        events.firstOrNull { it.id == newEvent.id } shouldNotBe null
    }

}