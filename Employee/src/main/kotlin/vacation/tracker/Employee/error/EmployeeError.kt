package vacation.tracker.Employee.error

import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.model.Employee

sealed class EmployeeError {

    object EmployeeNotFound : EmployeeError()
    object DuplicateVacationDates : EmployeeError()
    object VacationDateAlreadyExists : EmployeeError()
    data class UnexpectedError(val message: String) : EmployeeError()
}