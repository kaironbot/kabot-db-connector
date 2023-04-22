package org.wagham.db.models

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CalendarTest : StringSpec({

    "Can get the date from a Calendar instance" {
        val months = listOf(
            Month("January", 31, "Winter"),
            Month("February", 28, "Winter"),
            Month("March", 31, "Spring"),
            Month("April", 30, "Spring"),
            Month("May", 31, "Spring"),
            Month("June", 30, "Summer"),
            Month("July", 31, "Summer"),
            Month("August", 31, "Summer"),
            Month("September", 30, "Fall"),
            Month("October", 31, "Fall"),
            Month("November", 30, "Fall"),
            Month("December", 31, "Winter")
        )
        val calendar = Calendar("test_calendar", 2022, months)

        GameDate(2022, "January", 1, "Winter") shouldBe calendar.daysToDate(0)
        GameDate(2022, "January", 2, "Winter") shouldBe calendar.daysToDate(1)
        GameDate(2022, "February", 1, "Winter") shouldBe calendar.daysToDate(31)
        GameDate(2022, "February", 28, "Winter") shouldBe calendar.daysToDate(58)
        GameDate(2022, "March", 1, "Spring") shouldBe calendar.daysToDate(59)
        GameDate(2022, "March", 31, "Spring") shouldBe calendar.daysToDate(89)
        GameDate(2022, "May", 1, "Spring") shouldBe calendar.daysToDate(120)
        GameDate(2022, "June", 1, "Summer") shouldBe calendar.daysToDate(151)
        GameDate(2022, "July", 1, "Summer") shouldBe calendar.daysToDate(181)
        GameDate(2022, "August", 1, "Summer") shouldBe calendar.daysToDate(212)
        GameDate(2022, "September", 1, "Fall") shouldBe calendar.daysToDate(243)
        GameDate(2022, "October", 1, "Fall") shouldBe calendar.daysToDate(273)
        GameDate(2022, "November", 1, "Fall") shouldBe calendar.daysToDate(304)
        GameDate(2022, "December", 1, "Winter") shouldBe calendar.daysToDate(334)
        GameDate(2022, "December", 31, "Winter") shouldBe calendar.daysToDate(364)
        GameDate(2023, "January", 1, "Winter") shouldBe calendar.daysToDate(365)
        GameDate(2023, "February", 1, "Winter") shouldBe calendar.daysToDate(396)
        GameDate(2023, "January", 31, "Winter") shouldBe calendar.daysToDate(395)
        GameDate(2024, "April", 1, "Spring") shouldBe calendar.daysToDate(820)
    }

})