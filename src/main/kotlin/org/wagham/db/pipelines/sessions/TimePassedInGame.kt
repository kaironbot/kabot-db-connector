package org.wagham.db.pipelines.sessions

import org.litote.kmongo.*
import org.wagham.db.models.Session
import org.wagham.db.utils.dateAtMidnight
import java.util.*

data class TimePassedInGame(
    val days: Int
) {
    companion object {

        fun getPipeline(startDate: Date, endDate: Date) =
            listOf(
                match(
                    and(
                        Session::date gte dateAtMidnight(startDate),
                        Session::date lte dateAtMidnight(endDate)
                    )
                ),
                group(
                    id = Session::date,
                    TimePassedInGame::days max Session::duration
                )
            )
    }
}
