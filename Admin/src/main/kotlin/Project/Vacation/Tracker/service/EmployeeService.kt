package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.exeption.EmployeeExistsException
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.model.Vacation
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.results.EmployeeResult
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.stream.Collectors


@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val csvUtils: CsvUtils
) {

    fun createEmployee(employeeDTO: EmployeeDTO): Result<EmployeeResult, EmployeeResult> {

        if (employeeRepository.findByEmail(employeeDTO.email) != null) {

            return Err(EmployeeResult.DuplicateEmployee)
        }


        val employee = Employee(
            email = employeeDTO.email,
            password = employeeDTO.password
        )


        return Ok(EmployeeResult.Success(employeeRepository.save(employee)))

    }


    fun processAndSaveEmployees(file: MultipartFile): Result<String, EmployeeResult> {

        return runCatching {


            val employees = csvUtils.parseEmployees(file)
                .filter { isEmployeeUnique(it) }

            employeeRepository.saveAll(employees)
            "Employees imported successfully."
        }.mapError { e ->

            when (e) {

                is IOException -> EmployeeResult.FileParseError("Failed to read or parse CSV file: ${e.message}")
                else -> EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}")
            }


        }


    }


    private fun isEmployeeUnique(employee: Employee): Boolean {
        return employeeRepository.findByEmail(employee.email) == null
    }


}