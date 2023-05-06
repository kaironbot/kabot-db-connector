package org.wagham.db.utils

import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun dateAtMidnight(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

fun daysInBetween(startDate:Date, endDate: Date) =
    ChronoUnit.DAYS.between(
        startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
        endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    )