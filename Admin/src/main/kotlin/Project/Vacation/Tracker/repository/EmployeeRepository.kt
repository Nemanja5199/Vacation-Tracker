package Project.Vacation.Tracker.repository

import Project.Vacation.Tracker.model.Employee
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface EmployeeRepository : JpaRepository<Employee,Long> {

    fun findByEmail(email: String): Employee?

    fun existsByEmail(email: String): Boolean
}