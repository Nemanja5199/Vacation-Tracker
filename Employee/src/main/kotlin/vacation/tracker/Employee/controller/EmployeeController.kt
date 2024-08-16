package vacation.tracker.Employee.controller

import com.github.michaelbull.result.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.service.EmployeeService
import vacation.tracker.Employee.results.EmployeeResult
import java.time.LocalDate

@RestController
@RequestMapping("api/employee/")
class EmployeeController(private val employeeService: EmployeeService) {

    @GetMapping("/user")
    fun getEmployeeDetails(@RequestParam email: String): ResponseEntity<Any> {
        return employeeService.getEmployee(email).mapBoth(
            success = { result ->
                when (result) {
                    is EmployeeResult.EmployeeDTOResult -> ResponseEntity.ok(result.employeeDTO)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected success result")
                }
            },
            failure = { error ->
                when (error) {
                    is EmployeeResult.EmployeeNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee not found")
                    is EmployeeResult.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred")
                }
            }
        )
    }

    @GetMapping("/{email}/vacations")
    fun getAllVacations(@PathVariable email: String): ResponseEntity<Any> {
        return employeeService.getVacationDetails(email).mapBoth(
            success = { vacationDTOs ->
                ResponseEntity.ok(vacationDTOs)
            },
            failure = { error ->
                when (error) {
                    is EmployeeResult.EmployeeNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee not found")
                    is EmployeeResult.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred")
                }
            }
        )
    }

    @GetMapping("/vacation-dates")
    fun getVacationDates(
        @RequestParam email: String,
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate
    ): ResponseEntity<Any> {
        return employeeService.getVacationDatesDetails(email, fromDate, toDate).mapBoth(
            success = { vacationDatesDTOs ->
                ResponseEntity.ok(vacationDatesDTOs)
            },
            failure = { error ->
                when (error) {
                    is EmployeeResult.EmployeeNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee not found")
                    is EmployeeResult.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred")
                }
            }
        )
    }

    @PostMapping("/add-vacation-date")
    fun addVacationDates(@RequestBody vacationDatesDTO: VacationDatesDTO): ResponseEntity<String> {
        return employeeService.addVacationDates(vacationDatesDTO).mapBoth(
            success = {
                ResponseEntity.ok("Vacation dates added successfully")
            },
            failure = { error ->
                when (error) {
                    is EmployeeResult.EmployeeNotFound -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Employee not found")
                    is EmployeeResult.VacationDateAlreadyExists -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Vacation date already exists")
                    is EmployeeResult.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)
                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unexpected error occurred")
                }
            }
        )
    }
}
