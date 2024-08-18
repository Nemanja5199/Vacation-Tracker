package Project.Vacation.Tracker.repository

import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.model.Vacation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.Year
import java.util.*

interface VacationRepository : JpaRepository<Vacation,Long> {



    fun findByEmployeeEmailAndVacationYear(email: String, vacationYear: Int): Optional<Vacation>

    fun findByEmployeeAndVacationDaysAndVacationYear(employee: Employee, vacationDays: Int,
                                                     vacationYear: Int): Optional<Vacation>

    @Modifying
    @Transactional
    @Query("UPDATE Vacation v SET v.vacationDays = :days WHERE v.employee.email = :email")
    fun updateVacationDaysByEmail(@Param("email") email: String, @Param("days") days: Int): Int
}