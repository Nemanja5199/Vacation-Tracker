package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.model.Vacation
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.repository.VacationRepository
import Project.Vacation.Tracker.error.VacationError
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class VacationService(

    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository,
    private val csvUtils: CsvUtils
) {

    fun addVacationDaysToEmployee(email: String, days: Int, year: Int): Result<String, VacationError> {

        val employee = employeeRepository.findByEmail(email)
            ?: return Err(VacationError.EmployeeNotFound)

        val existingVacation = vacationRepository.findByEmployeeAndVacationDaysAndVacationYear(employee, days, year)
        return if (existingVacation != null)
            Err(VacationError.DuplicateVacation)
        else {
            val vacation = Vacation(vacationDays = days, employee = employee, vacationYear = year)
            vacationRepository.save(vacation)
            Ok("Vacation for $employee added successfully")
        }


    }


    fun proccesAndSaveVacations(file: MultipartFile): Result<String, VacationError> = runCatching {
        val vacationsDTOResult = csvUtils.parseVacations(file)
        val vacations = mutableListOf<Vacation>()


        vacationsDTOResult.mapBoth(
            success = { vacationDTOs ->
                vacationDTOs.forEach { vacationDTO ->

                    val employee = employeeRepository.findByEmail(vacationDTO.email)
                        ?: return Err(VacationError.EmployeeNotFound)


                    val vacation = Vacation(
                        vacationDays = vacationDTO.vacationDays,
                        employee = employee,
                        vacationYear = vacationDTO.vacationYear
                    )
                    vacations.add(vacation)
                }


                val uniqueVacations = vacations.filter { isVacationUnique(it) }


                if (uniqueVacations.isNotEmpty()) {
                    vacationRepository.saveAll(uniqueVacations)
                    Ok("Vacations imported successfully.")
                } else {
                    Err(VacationError.NoVacationsToImport)
                }
            },
            failure = { error ->

                when (error) {
                    is VacationError.FileParseError -> Err(VacationError.FileParseError("Failed to read or parse CSV file: ${error.message}"))
                    is VacationError.InvalidDataError -> Err(VacationError.InvalidDataError("Invalid data in CSV file: ${error.message}"))
                    else -> Err(VacationError.UnexpectedError("An unexpected error occurred"))
                }
            }
        )
    }.getOrElse { e ->

        Err(VacationError.UnexpectedError("An unexpected error occurred: ${e.message}"))
    }


    private fun isVacationUnique(vacation: Vacation): Boolean {
        return vacationRepository.findByEmployeeEmailAndVacationYear(
            vacation.employee.email,
            vacation.vacationYear
        ) == null
    }


}










