package Project.Vacation.Tracker.controller

import Project.Vacation.Tracker.result.VacationDateResult
import Project.Vacation.Tracker.result.VacationResult
import Project.Vacation.Tracker.service.VacationService
import com.github.michaelbull.result.mapBoth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/vacation")
class VacationController(private val vacationService: VacationService) {


    @PostMapping("/")
    fun setVacationDays(
        @RequestParam("email") email: String,
        @RequestParam("days") days: Int,
        @RequestParam("year") year: Int
    ): ResponseEntity<Any> {
        val result = vacationService.addVacationDaysToEmployee(email, days, year)

        return result.mapBoth(
            success = { vacation ->
                ResponseEntity.status(HttpStatus.CREATED)
                    .body("Vacation added successfully for ${email}")
            },


            failure = { error ->


                when (error) {

                    is VacationResult.EmployeeNotFound -> ResponseEntity.status((HttpStatus.NOT_FOUND))
                        .body("Employee not found")

                    is VacationResult.DuplicateVacation -> ResponseEntity.status((HttpStatus.CONFLICT))
                        .body("Vacation already exists")

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unknown error occurred")

                }


            }

        )


    }


    @PostMapping("/import")
    fun importVacations(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {

        val result = vacationService.proccesAndSaveVacations(file)

        return result.mapBoth(

            success = { message -> ResponseEntity.ok(message) },

            failure = { error ->

                when (error) {

                    is VacationResult.FileParseError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is VacationResult.InvalidDataError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is VacationResult.UnexpectedError -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error.message)

                    is VacationResult.NoVacationsToImport -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No vacation dates to import")

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unknown error occurred")
                }

            }


        )


    }


}