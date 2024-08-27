package Project.Vacation.Tracker.error

import java.time.LocalDate

sealed class VacationDateError {

    object EmployeeNotFound : VacationDateError()
    object NoVacationsToImport : VacationDateError()
    data class OverlappingVacation(val email: String) : VacationDateError()
    data class DuplicateStartDate(val email: String, val startDate: LocalDate) : VacationDateError()
    data class InvalidDataError(val message: String) : VacationDateError()
    data class FileParseError(val message: String) : VacationDateError()
    data class UnexpectedError(val message: String) : VacationDateError()
    data class ImportFailed(val message: String) : VacationDateError()
    data class InvalidVacationPeriod(val email: String, val startDate: LocalDate, val endDate: LocalDate) :
        VacationDateError()
}