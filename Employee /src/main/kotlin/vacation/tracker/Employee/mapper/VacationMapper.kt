package vacation.tracker.Employee.mapper


import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.model.Employee
import vacation.tracker.Employee.model.Vacation
import vacation.tracker.Employee.model.VacationDates

object VacationMapper {


    fun toEmployeeDTO(employee: Employee): EmployeeDTO {
        return EmployeeDTO(
            email = employee.email,
            password = employee.password
        )
    }



    fun toVacationDTOList(vacations: List<Vacation>, vacationDatesList: List<VacationDates>): List<VacationDTO> {
        return vacations.map { vacation ->
            val vacationDates = vacationDatesList.filter { it.employee.email == vacation.employee.email }
            toVacationDTO(vacation, vacationDates)
        }
    }


    private fun toVacationDTO(vacation: Vacation, vacationDates: List<VacationDates>): VacationDTO {
        val usedDays = calculateUsedDays(vacationDates)
        return VacationDTO(
            email = vacation.employee.email,
            totalDays = vacation.vacationDays,
            year = vacation.vacationYear,
            availableDays = vacation.vacationDays - usedDays,
            usedDays = usedDays
        )
    }


   private fun toVacationDatesDTO(vacationDates: VacationDates): VacationDatesDTO {
        return VacationDatesDTO(


            startDate = vacationDates.startDate,
            endDate = vacationDates.endDate,
            email = vacationDates.employee.email
        )
    }


    fun toVacationDatesDTOList(vacationDatesList: List<VacationDates>): List<VacationDatesDTO> {
        return vacationDatesList.map { toVacationDatesDTO(it) }
    }



    private fun calculateUsedDays(vacationDates: List<VacationDates>): Int {
        return vacationDates.sumOf { vacationDate ->
            var days = 0
            var currentDate = vacationDate.startDate

            while (!currentDate.isAfter(vacationDate.endDate)) {

                if (currentDate.dayOfWeek != java.time.DayOfWeek.SATURDAY && currentDate.dayOfWeek != java.time.DayOfWeek.SUNDAY) {
                    days++
                }

                currentDate = currentDate.plusDays(1)
            }
            days
        }
    }

}