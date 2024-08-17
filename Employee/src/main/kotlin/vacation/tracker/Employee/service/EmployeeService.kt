package vacation.tracker.Employee.service

import com.fasterxml.jackson.annotation.Nulls
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.mapper.VacationMapper
import vacation.tracker.Employee.model.Employee
import vacation.tracker.Employee.model.VacationDates
import vacation.tracker.Employee.repository.EmployeeRepository
import vacation.tracker.Employee.repository.VacationDatesRepository
import vacation.tracker.Employee.repository.VacationRepository
import vacation.tracker.Employee.results.EmployeeResult
import java.time.LocalDate

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository,
    private val vacationDatesRepository: VacationDatesRepository
) {

    fun getEmployee(email: String): Result<EmployeeResult, EmployeeResult> {
        return runCatching {
            val employee = employeeRepository.findByEmail(email)
                ?: throw NoSuchElementException("Employee not found")

            EmployeeResult.EmployeeDTOResult(VacationMapper.toEmployeeDTO(employee))
        }.mapBoth(
            success = { Ok(it) },
            failure = { e ->
                when (e) {
                    is NoSuchElementException -> Err(EmployeeResult.EmployeeNotFound)
                    else -> Err(EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }
            }
        )
    }

    fun getVacationDetails(email: String): Result<List<VacationDTO>, EmployeeResult> {
        return runCatching {
            val checkEmployee = employeeRepository.existsByEmail(email)
            if(!checkEmployee){
                throw NoSuchElementException("Employee not found")
            }

            val vacations = vacationRepository.findAllByEmployeeEmail(email)
            val vacationDates = vacationDatesRepository.findByEmployeeEmail(email)
            VacationMapper.toVacationDTOList(vacations, vacationDates)
        }.mapBoth(
            success = { Ok(it) },
            failure = { e ->
                when(e){

                    is NoSuchElementException -> Err(EmployeeResult.EmployeeNotFound)
                    else -> Err(EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }

            }
        )
    }

    fun getVacationDatesDetails(email: String, fromDate: LocalDate, toDate: LocalDate): Result<List<VacationDatesDTO>, EmployeeResult> {
        return runCatching {

            val checkEmployee = employeeRepository.existsByEmail(email)

            if(!checkEmployee){
                 throw NoSuchElementException("Employee not found")
            }

            val vacationDates = vacationDatesRepository.findByEmployeeEmailAndStartDateBetween(email, fromDate, toDate)


            VacationMapper.toVacationDatesDTOList(vacationDates)
        }.mapBoth(
            success = { Ok(it) },
            failure = { e ->

                when(e){

                    is NoSuchElementException -> Err(EmployeeResult.EmployeeNotFound)
                    else -> Err(EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}")) }
                }

        )
    }

    fun addVacationDates(vacationDatesDTO: VacationDatesDTO): Result<Boolean, EmployeeResult> {
        return runCatching {
            val employee = employeeRepository.findByEmail(vacationDatesDTO.email)
                ?: throw NoSuchElementException("Employee not found")

            val existingDates = vacationDatesRepository.findByEmployeeEmailAndStartDateBetween(
                email = vacationDatesDTO.email,
                startDate = vacationDatesDTO.startDate,
                endDate = vacationDatesDTO.endDate
            )

            if (existingDates.isNotEmpty()) {
                throw IllegalArgumentException("Vacation date already exists")
            }

            val vacationDate = VacationDates(
                startDate = vacationDatesDTO.startDate,
                endDate = vacationDatesDTO.endDate,
                employee = employee
            )

            vacationDatesRepository.save(vacationDate)
            true
        }.mapBoth(
            success = { Ok(it) },
            failure = { e ->
                when (e) {
                    is NoSuchElementException -> Err(EmployeeResult.EmployeeNotFound)
                    is IllegalArgumentException -> Err(EmployeeResult.DuplicateVacationDates)
                    else -> Err(EmployeeResult.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }
            }
        )
    }
}
