package vacation.tracker.Employee.repository

import org.springframework.data.jpa.repository.JpaRepository
import vacation.tracker.Employee.model.Vacation
import vacation.tracker.Employee.model.VacationDates
import java.time.LocalDate

interface VacationDatesRepository : JpaRepository<VacationDates,Long> {


    fun findByEmployeeEmail( email : String) : List<VacationDates>

    fun findByEmployeeEmailAndStartDateBetween(email: String, startDate: LocalDate, endDate: LocalDate): List<VacationDates>
}