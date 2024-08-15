package vacation.tracker.Employee.repository

import org.springframework.data.jpa.repository.JpaRepository
import vacation.tracker.Employee.model.Vacation

interface VacationRepository : JpaRepository<Vacation,Long> {


    fun findAllByEmployeeEmail( email: String) : List<Vacation>
}