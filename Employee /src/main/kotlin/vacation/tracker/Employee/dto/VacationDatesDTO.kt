package vacation.tracker.Employee.dto

import java.time.LocalDate

data class VacationDatesDTO(

    val endDate : LocalDate,
    val startDate : LocalDate,

    val email : String

)