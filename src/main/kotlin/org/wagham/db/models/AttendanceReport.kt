package org.wagham.db.models

import org.bson.codecs.pojo.annotations.BsonId
import org.wagham.db.models.embed.AttendanceReportPlayer
import java.util.Date

data class AttendanceReport(
    @BsonId val date: Date,
    val message: String,
    val players: Map<String, List<AttendanceReportPlayer>> = emptyMap(),
    val afternoonPlayers: Map<String, List<AttendanceReportPlayer>> = emptyMap()
)
