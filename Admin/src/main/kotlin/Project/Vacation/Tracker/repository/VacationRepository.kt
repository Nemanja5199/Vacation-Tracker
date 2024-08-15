package Project.Vacation.Tracker.repository

import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.model.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Year
import java.util.*

interface VacationRepository : JpaRepository<Vacation,Long> {

    fun findByEmployeeEmailAndVacationYear(email: String, vacationYear: Int): Optional<Vacation>

    fun findByEmployeeAndVacationDaysAndVacationYear(employee: Employee, vacationDays: Int,
                                                     vacationYear: Int): Optional<Vacation>
}