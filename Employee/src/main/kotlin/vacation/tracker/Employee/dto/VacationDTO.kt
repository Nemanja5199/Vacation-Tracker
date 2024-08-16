package vacation.tracker.Employee.dto

data class VacationDTO(

    val email: String,

    val totalDays: Int,

    val year : Int,

    val usedDays : Int,

    val availableDays : Int

)
