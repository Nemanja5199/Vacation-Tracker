package Project.Vacation.Tracker.controller

import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.error.EmployeeError
import Project.Vacation.Tracker.service.EmployeeService
import com.github.michaelbull.result.mapBoth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/admin/employees")
class EmployeeController(private val employeeService: EmployeeService) {


    @PostMapping
    fun createEmployee(@RequestBody employeeDTO: EmployeeDTO): ResponseEntity<Any> {

        val result = employeeService.createEmployee(employeeDTO)

        return result.mapBoth(

            success = { employeeResult ->
                ResponseEntity.status(HttpStatus.CREATED)
                    .body("Employee with username ${employeeDTO.email} added successfully")
            },

            failure = { error ->

                when (error) {
                    is EmployeeError.DuplicateEmployee -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Employee already exists")

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unknown error occurred")
                }
            }

        )


    }


    @PostMapping("/import")
    fun importEmployees(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {

        val result = employeeService.processAndSaveEmployees(file)

        return result.mapBoth(

            success = { message -> ResponseEntity.ok(message) },
            failure = { error ->

                when (error) {
                    is EmployeeError.FileParseError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is EmployeeError.InvalidDataError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is EmployeeError.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unknown error occurred")
                }

            }

        )

    }


}