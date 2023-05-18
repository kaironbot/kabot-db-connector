package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.enums.ScheduledEventArg
import org.wagham.db.enums.ScheduledEventState
import org.wagham.db.enums.ScheduledEventType
import java.util.Date

data class ScheduledEvent(
    @BsonId val id: String,
    val type: ScheduledEventType,
    val created: Date,
    val activation: Date,
    val state: ScheduledEventState,
    val args: Map<ScheduledEventArg, String>
)
