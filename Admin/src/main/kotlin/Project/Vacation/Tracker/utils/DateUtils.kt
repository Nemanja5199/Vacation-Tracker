package Project.Vacation.Tracker.utils

import java.time.DayOfWeek
import java.time.LocalDate

object DateUtils {

    fun calculateWorkingDays(startDate: LocalDate, endDate: LocalDate): Int {
        var workDays = 0
        var date = startDate

        while (!date.isAfter(endDate)) {
            if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
                workDays++
            }
            date = date.plusDays(1)
        }

        return workDays
    }


}