package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.model.VacationDates
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.repository.VacationDatesRepository
import Project.Vacation.Tracker.repository.VacationRepository
import Project.Vacation.Tracker.error.VacationDateError
import Project.Vacation.Tracker.utils.CsvUtils
import Project.Vacation.Tracker.utils.DateUtils
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapBoth
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class VacationDatesService(
    private val vacationDatesRepository: VacationDatesRepository,
    private val csvUtils: CsvUtils,
    private val vacationRepository: VacationRepository,
    private val employeeRepository: EmployeeRepository
) {


    fun processAndSaveVacationDates(file: MultipartFile): Result<String, VacationDateError> = runCatching {
        val vacationDatesDTOResult = csvUtils.parseVacationDates(file)
        val newVacationDates = mutableListOf<VacationDates>()
        val availableDaysMap = mutableMapOf<Pair<String, Int>, Int>()

        vacationDatesDTOResult.mapBoth(
            success = { vacationDatesDTOs ->
                vacationDatesDTOs.forEach { vacationDatesDTO ->

                    val employee = employeeRepository.findByEmail(vacationDatesDTO.email)
                        ?: return Err(VacationDateError.EmployeeNotFound)

                    val vacationDates = VacationDates(
                        employee = employee,
                        startDate = vacationDatesDTO.startDate,
                        endDate = vacationDatesDTO.endDate
                    )


                    val existingVacations = vacationDatesRepository.findByEmployeeEmail(vacationDates.employee.email)


                    val overlaps = existingVacations.any { existing ->
                        vacationDates.startDate.isBefore(existing.endDate) && vacationDates.endDate.isAfter(existing.startDate)
                    }

                    if (overlaps) {
                        return Err(VacationDateError.OverlappingVacation(vacationDates.employee.email))
                    }


                    val startDateExists = existingVacations.any { it.startDate == vacationDates.startDate }
                    if (startDateExists) {
                        return Err(
                            VacationDateError.DuplicateStartDate(
                                vacationDates.employee.email,
                                vacationDates.startDate
                            )
                        )
                    }


                    val days = DateUtils.calculateWorkingDays(vacationDates.startDate, vacationDates.endDate)


                    val remainingDays = checkEmployeeDats(
                        vacationDates.employee.email,
                        days,
                        vacationDates.startDate.year,
                        availableDaysMap
                    )

                    if (!remainingDays) {

                        return@forEach
                    }

                    newVacationDates.add(vacationDates)
                }


                if (newVacationDates.isNotEmpty()) {
                    vacationDatesRepository.saveAll(newVacationDates)
                    Ok("Vacation dates imported successfully.")
                } else {
                    Err(VacationDateError.NoVacationsToImport)
                }
            },
            failure = { error ->
                when (error) {
                    is VacationDateError.FileParseError -> Err(VacationDateError.FileParseError("Failed to read or parse CSV file: ${error.message}"))
                    is VacationDateError.InvalidDataError -> Err(VacationDateError.InvalidDataError("Invalid data in CSV file: ${error.message}"))
                    else -> Err(VacationDateError.UnexpectedError("An unexpected error occurred"))
                }
            }
        )
    }.getOrElse { e ->

        Err(VacationDateError.UnexpectedError("An unexpected error occurred: ${e.message}"))
    }


    fun checkEmployeeDats(
        email: String,
        days: Int,
        year: Int,
        availableDaysMap: MutableMap<Pair<String, Int>, Int>
    ): Boolean {
        val vacation = vacationRepository.findByEmployeeEmailAndVacationYear(email, year)

        val key = Pair(email, year)

        if (vacation == null) {

            return false
        }

        val availableDays = availableDaysMap.getOrDefault(key, vacation.vacationDays)
        if (availableDays < days) {
            return false
        }
        availableDaysMap[key] = availableDays - days
        vacationRepository.save(vacation)
        return true
    }


}