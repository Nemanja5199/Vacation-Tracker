package vacation.tracker.Employee.results

import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.model.Employee

sealed class EmployeeResult {


    data class Success(val employee: Employee) : EmployeeResult()
    data class EmployeeDTOResult(val employeeDTO: EmployeeDTO) : EmployeeResult()
    data class VacationDTOListResult(val vacationDTOList: List<VacationDTO>) : EmployeeResult()
    data class VacationDatesDTOListResult(val vacationDatesDTOList: List<VacationDatesDTO>) : EmployeeResult()
    object EmployeeNotFound : EmployeeResult()
    object DuplicateVacationDates : EmployeeResult()
    object VacationDateAlreadyExists : EmployeeResult()
    data class UnexpectedError(val message: String) : EmployeeResult()
}