package Project.Vacation.Tracker.result

import Project.Vacation.Tracker.model.Vacation

sealed class VacationResult {

    data class Success(val vacation: Vacation) : VacationResult()
    object EmployeeNotFound : VacationResult()
    object DuplicateVacation : VacationResult()
    object NoVacationsToImport: VacationResult()
    data class InvalidDataError(val message: String) :VacationResult()
    data class FileParseError(val message: String) : VacationResult()
    data class UnexpectedError(val message: String) : VacationResult()
}