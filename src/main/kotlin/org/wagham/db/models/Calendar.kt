package org.wagham.db.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

data class Month(
    val name: String,
    val days: Int,
    val season: String
) {
    fun getDay(index: Int) = index + 1
}

data class Calendar(
    @BsonId val calendarId: String,
    @JsonProperty("starting_year") val startingYear: Int = 0,
    val months: List<Month> = emptyList()
) {

    private val daysInAYear = months.sumOf { it.days }

    fun daysToDate(days: Long): GameDate {
        val yearsPassed = days/daysInAYear
        val daysLeft = days % daysInAYear
        val month = months.drop(1).fold(Triple(months.first(), daysLeft, daysLeft - months.first().days)) { acc, it ->
            if (acc.third < 0) acc
            else Triple(it, acc.second - acc.first.days, acc.third - it.days)
        }
        return GameDate(
            startingYear + yearsPassed.toInt(),
            month.first.name,
            month.first.getDay(month.second.toInt()),
            month.first.season
        )
    }

}