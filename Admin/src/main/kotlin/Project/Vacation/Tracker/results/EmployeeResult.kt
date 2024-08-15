package Project.Vacation.Tracker.results

import Project.Vacation.Tracker.model.Employee

sealed class EmployeeResult {

    data class Success(val employee: Employee) : EmployeeResult()
    object EmployeeNotFound : EmployeeResult()
    object DuplicateEmployee : EmployeeResult()
    data class FileParseError(val message: String) : EmployeeResult()
    data class UnexpectedError(val message: String) : EmployeeResult()

}