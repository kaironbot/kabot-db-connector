package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import java.util.Date

data class AttendanceReport(
    @BsonId val date: Date,
    val message: String,
    val players: Map<String, Int> = emptyMap()
)
