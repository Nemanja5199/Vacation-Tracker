package Project.Vacation.Tracker.repository

import Project.Vacation.Tracker.model.VacationDates
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.Optional

interface VacationDatesRepository : JpaRepository<VacationDates,Long> {

    fun findByEmployeeEmailAndStartDateAndEndDate(
        email: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): VacationDates?


    fun findByEmployeeEmail(email: String): List<VacationDates>

}