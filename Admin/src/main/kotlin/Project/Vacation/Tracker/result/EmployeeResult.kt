package Project.Vacation.Tracker.result

import Project.Vacation.Tracker.model.Employee

sealed class EmployeeResult {

    data class Success(val employee: Employee) : EmployeeResult()
    object EmployeeNotFound : EmployeeResult()
    object DuplicateEmployee : EmployeeResult()
    data class InvalidDataError(val message: String) :EmployeeResult()
    data class FileParseError(val message: String) : EmployeeResult()
    data class UnexpectedError(val message: String) : EmployeeResult()

}