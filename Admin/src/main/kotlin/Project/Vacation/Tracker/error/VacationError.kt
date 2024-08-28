package Project.Vacation.Tracker.error

import Project.Vacation.Tracker.model.Vacation

sealed class VacationError {

    object EmployeeNotFound : VacationError()
    object DuplicateVacation : VacationError()
    object NoVacationsToImport: VacationError()
    data class InvalidDataError(val message: String) :VacationError()
    data class FileParseError(val message: String) : VacationError()
    data class UnexpectedError(val message: String) : VacationError()
}