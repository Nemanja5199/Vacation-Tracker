package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.result.EmployeeResult
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val csvUtils: CsvUtils
) {

    fun createEmployee(employeeDTO: EmployeeDTO): Result<String, EmployeeResult> {

        if (employeeRepository.findByEmail(employeeDTO.email) != null) {

            return Err(EmployeeResult.DuplicateEmployee)
        }


        val employee = Employee(
            email = employeeDTO.email,
            password = employeeDTO.password
        )

        employeeRepository.save(employee)

      return  Ok("Employee added")

    }


    fun processAndSaveEmployees(file: MultipartFile): Result<String, EmployeeResult> = runCatching {


        val employees = csvUtils.parseEmployees(file)

        employees.mapBoth(
            success = { employees ->
                employees.filter { isEmployeeUnique(it) }
                employeeRepository.saveAll(employees)
            },

            failure = { error ->

                when (error) {
                    is EmployeeResult.FileParseError -> EmployeeResult.FileParseError("Failed to read or parse CSV file: ${error.message}")
                    is EmployeeResult.InvalidDataError -> EmployeeResult.InvalidDataError("Invalid data in CSV file: ${error.message}")
                    else -> EmployeeResult.UnexpectedError("An unknown error occurred")
                }

            }

        )

        Ok("Employees imported successfully.")
    }.getOrElse { e ->

        Err(EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}"))
    }


    private fun isEmployeeUnique(employee: Employee): Boolean {
        return employeeRepository.findByEmail(employee.email) == null
    }


}