package Project.Vacation.Tracker.error

import Project.Vacation.Tracker.model.Employee

sealed class EmployeeError {

    object EmployeeNotFound : EmployeeError()
    object DuplicateEmployee : EmployeeError()
    data class InvalidDataError(val message: String) :EmployeeError()
    data class FileParseError(val message: String) : EmployeeError()
    data class UnexpectedError(val message: String) : EmployeeError()

}