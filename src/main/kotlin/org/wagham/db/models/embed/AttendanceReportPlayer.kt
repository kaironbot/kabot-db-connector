package org.wagham.db.models.embed

data class AttendanceReportPlayer(
    val daysSinceLastPlayed: Int,
    val tiers: List<String>
)
