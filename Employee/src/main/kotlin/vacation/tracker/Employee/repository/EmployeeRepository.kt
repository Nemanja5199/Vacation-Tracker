package vacation.tracker.Employee.repository

import org.springframework.data.jpa.repository.JpaRepository
import vacation.tracker.Employee.EmployeeApplication
import vacation.tracker.Employee.model.Employee
import java.util.Optional

interface EmployeeRepository : JpaRepository<Employee,Long>{

    fun findByEmail(email: String): Employee?

    fun existsByEmail(email: String) : Boolean

}