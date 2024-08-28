package Project.Vacation.Tracker.results

import java.time.LocalDate

sealed class VacationDateResult {

    object Success : VacationDateResult()
    data class OverlappingVacation(val email: String) : VacationDateResult()
    data class DuplicateStartDate(val email: String, val startDate: LocalDate) : VacationDateResult()
    data class ImportFailed(val message: String) : VacationDateResult()
    data class InvalidVacationPeriod(val email: String, val startDate: LocalDate, val endDate: LocalDate) : VacationDateResult()
}