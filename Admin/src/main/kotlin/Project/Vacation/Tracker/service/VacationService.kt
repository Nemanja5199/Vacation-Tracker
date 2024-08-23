package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.model.Vacation
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.repository.VacationRepository
import Project.Vacation.Tracker.result.EmployeeResult
import Project.Vacation.Tracker.result.VacationResult
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Service
class VacationService(

    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository,
    private val csvUtils: CsvUtils
) {

    fun addVacationDaysToEmployee(email: String, days: Int, year: Int): Result<VacationResult, VacationResult> {

        val employee = employeeRepository.findByEmail(email)
            ?: return Err(VacationResult.EmployeeNotFound)

        val existingVacation = vacationRepository.findByEmployeeAndVacationDaysAndVacationYear(employee, days, year)
        return if (existingVacation != null)
            Err(VacationResult.DuplicateVacation)
        else {
            val vacation = Vacation(vacationDays = days, employee = employee, vacationYear = year)
            Ok(VacationResult.Success(vacationRepository.save(vacation)))
        }


    }


    fun proccesAndSaveVacations(file: MultipartFile): Result<String, VacationResult> = runCatching {
        val vacationsDTOResult = csvUtils.parseVacations(file)
        val vacations = mutableListOf<Vacation>()


        vacationsDTOResult.mapBoth(
            success = { vacationDTOs ->
                vacationDTOs.forEach { vacationDTO ->

                    val employee = employeeRepository.findByEmail(vacationDTO.email)
                        ?: return Err(VacationResult.EmployeeNotFound)


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
                    Err(VacationResult.NoVacationsToImport)
                }
            },
            failure = { error ->

                when (error) {
                    is VacationResult.FileParseError -> Err(VacationResult.FileParseError("Failed to read or parse CSV file: ${error.message}"))
                    is VacationResult.InvalidDataError -> Err(VacationResult.InvalidDataError("Invalid data in CSV file: ${error.message}"))
                    else -> Err(VacationResult.UnexpectedError("An unexpected error occurred"))
                }
            }
        )
    }.getOrElse { e ->

        Err(VacationResult.UnexpectedError("An unexpected error occurred: ${e.message}"))
    }


    private fun isVacationUnique(vacation: Vacation): Boolean {
        return vacationRepository.findByEmployeeEmailAndVacationYear(
            vacation.employee.email,
            vacation.vacationYear
        ) == null
    }


}










