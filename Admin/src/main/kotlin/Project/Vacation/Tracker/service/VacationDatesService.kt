package Project.Vacation.Tracker.service

import Project.Vacation.Tracker.model.VacationDates
import Project.Vacation.Tracker.repository.VacationDatesRepository
import Project.Vacation.Tracker.repository.VacationRepository
import Project.Vacation.Tracker.results.VacationDateResult
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate


@Service
class VacationDatesService(private val vacationDatesRepository: VacationDatesRepository,
                           private val csvUtils: CsvUtils,
                           private val vacationRepository: VacationRepository) {





    fun processAndSaveVacationDates( file:MultipartFile):Result<VacationDateResult,VacationDateResult>{

            val vacationDates = csvUtils.parseVacationDates(file)
            val newVacationDates = mutableListOf<VacationDates>()
            val availableDaysMap = mutableMapOf<Pair<String, Int>, Int>()


        vacationDates.forEach{ vacationDates ->

            val existingVacations = vacationDatesRepository.findByEmployeeEmail(vacationDates.employee.email)


            val overlaps = existingVacations.any{ existing ->
                vacationDates.startDate.isBefore(existing.endDate) && vacationDates.endDate.isAfter(existing.startDate)
            }

            if (overlaps) {
                return Err(VacationDateResult.OverlappingVacation(vacationDates.employee.email))
            }

            val startDateExists = existingVacations.any { it.startDate == vacationDates.startDate }
            if (startDateExists) {
                return Err(VacationDateResult.DuplicateStartDate(vacationDates.employee.email, vacationDates.startDate))
            }


            val days = calculateWorkingDays(vacationDates.startDate,vacationDates.endDate)

            val remaingDays= checkEmployeeDats(vacationDates.employee.email,days,vacationDates.startDate.year,availableDaysMap)

            if(!remaingDays){
                return@forEach
            }

            newVacationDates.add(vacationDates)
        }

        vacationDatesRepository.saveAll(newVacationDates)
        return Ok(VacationDateResult.Success)



    }


    fun checkEmployeeDats(email: String,days:Int,year:Int,availableDaysMap: MutableMap<Pair<String, Int>, Int>): Boolean {
        val vacation = vacationRepository.findByEmployeeEmailAndVacationYear(email,year)
            .orElse(null)

        val key = Pair(email,year)

        if (vacation == null) {

            return false
        }

        val availableDays = availableDaysMap.getOrDefault(key, vacation.vacationDays)
        if (availableDays < days) {
            return false
        }
        availableDaysMap[key]= availableDays-days
        vacationRepository.save(vacation)
        return true
    }



    fun calculateWorkingDays(startDate: LocalDate, endDate: LocalDate): Int {
        var workDays = 0
        var date = startDate

        while (!date.isAfter(endDate)) {
            if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY) {
                workDays++
            }
            date = date.plusDays(1)
        }

        return workDays
    }


}