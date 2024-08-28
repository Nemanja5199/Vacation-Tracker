package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.model.Vacation
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.repository.VacationRepository
import Project.Vacation.Tracker.results.VacationResult
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
)

{

    fun addVacationDaysToEmployee( email:String , days: Int, year: Int) :Result<VacationResult,VacationResult>{

        val employee = employeeRepository.findByEmail(email).orElse(null)
            ?: return Err(VacationResult.EmployeeNotFound)

            val existingVacation = vacationRepository.findByEmployeeAndVacationDaysAndVacationYear(employee, days, year)
           return  if(existingVacation.isPresent)
                Err(VacationResult.DuplicateVacation)
            else{
                val vacation = Vacation(vacationDays = days, employee = employee, vacationYear = year)
                Ok(VacationResult.Success(vacationRepository.save(vacation)))
            }


    }



    fun proccesAndSaveVacations(file : MultipartFile) : Result<String,VacationResult>{

        return  runCatching {
            val vacations = csvUtils.parseVacations(file)
                .filter { isVacationUnique(it) }

            if (vacations.isNotEmpty()) {
                vacationRepository.saveAll(vacations)
            }
             "Vacations imported successfully."
            }.mapError { e ->

                when(e) {

                    is IOException -> VacationResult.FileParseError("Failed to read or parse CSV file: ${e.message}")
                    else -> VacationResult.UnexpectedError("An unexpected error occurred: ${e.message}")

                }



        }




        }


    private fun isVacationUnique(vacation: Vacation): Boolean {
        return vacationRepository.findByEmployeeEmailAndVacationYear(vacation.employee.email, vacation.vacationYear).isEmpty
    }



}










