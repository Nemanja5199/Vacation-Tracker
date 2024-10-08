package Project.Vacation.Tracker.controller


import Project.Vacation.Tracker.error.VacationDateError
import Project.Vacation.Tracker.service.VacationDatesService
import com.github.michaelbull.result.mapBoth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/vacation-dates")
class VacationDatesController(private val vacationDatesService: VacationDatesService) {


    @PostMapping("/import")
    fun importVacationDates(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val result = vacationDatesService.processAndSaveVacationDates(file)

        return result.mapBoth(
            success = { message ->
                ResponseEntity.ok(message)
            },
            failure = { error ->
                when (error) {
                    is VacationDateError.OverlappingVacation -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Vacation date overlaps for employee ${error.email}.")

                    is VacationDateError.DuplicateStartDate -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Duplicate start date ${error.startDate} for employee ${error.email}.")

                    is VacationDateError.InvalidVacationPeriod -> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid vacation period for employee ${error.email}.")

                    is VacationDateError.FileParseError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is VacationDateError.InvalidDataError -> ResponseEntity.badRequest()
                        .body(error.message)

                    is VacationDateError.NoVacationsToImport -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No vacation dates to import")

                    else -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("An unknown error occurred.")
                }
            }
        )
    }


}