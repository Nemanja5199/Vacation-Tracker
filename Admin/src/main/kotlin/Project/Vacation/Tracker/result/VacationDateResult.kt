package Project.Vacation.Tracker.result

import java.time.LocalDate

sealed class VacationDateResult {

    object EmployeeNotFound : VacationDateResult()
    object NoVacationsToImport : VacationDateResult()
    data class OverlappingVacation(val email: String) : VacationDateResult()
    data class DuplicateStartDate(val email: String, val startDate: LocalDate) : VacationDateResult()
    data class InvalidDataError(val message: String) : VacationDateResult()
    data class FileParseError(val message: String) : VacationDateResult()
    data class UnexpectedError(val message: String) : VacationDateResult()
    data class ImportFailed(val message: String) : VacationDateResult()
    data class InvalidVacationPeriod(val email: String, val startDate: LocalDate, val endDate: LocalDate) :
        VacationDateResult()
}