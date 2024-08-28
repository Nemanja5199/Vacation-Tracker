package vacation.tracker.Employee.service

import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import vacation.tracker.Employee.dto.EmployeeDTO
import vacation.tracker.Employee.dto.VacationDTO
import vacation.tracker.Employee.dto.VacationDatesDTO
import vacation.tracker.Employee.mapper.VacationMapper
import vacation.tracker.Employee.model.VacationDates
import vacation.tracker.Employee.repository.EmployeeRepository
import vacation.tracker.Employee.repository.VacationDatesRepository
import vacation.tracker.Employee.repository.VacationRepository
import vacation.tracker.Employee.error.EmployeeError
import vacation.tracker.Employee.model.Employee
import java.time.LocalDate

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val vacationRepository: VacationRepository,
    private val vacationDatesRepository: VacationDatesRepository
) {

    fun getEmployee(email: String): Result<EmployeeDTO, EmployeeError> {
        return runCatching {
            val employee = employeeRepository.findByEmail(email)
                ?: throw NoSuchElementException("Employee not found")

            VacationMapper.toEmployeeDTO(employee)
        }.mapBoth(
            success = { Ok(it) },
            failure = { e ->
                when (e) {
                    is NoSuchElementException -> Err(EmployeeError.EmployeeNotFound)
                    else -> Err(EmployeeError.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }
            }
        )
    }

    fun getVacationDetails(email: String): Result<List<VacationDTO>, EmployeeError> {
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

                    is NoSuchElementException -> Err(EmployeeError.EmployeeNotFound)
                    else -> Err(EmployeeError.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }

            }
        )
    }

    fun getVacationDatesDetails(email: String, fromDate: LocalDate, toDate: LocalDate): Result<List<VacationDatesDTO>, EmployeeError> {
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

                    is NoSuchElementException -> Err(EmployeeError.EmployeeNotFound)
                    else -> Err(EmployeeError.UnexpectedError("An unexpected error occurred: ${e.message}")) }
                }

        )
    }

    fun addVacationDates(vacationDatesDTO: VacationDatesDTO): Result<Boolean, EmployeeError> {
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
                    is NoSuchElementException -> Err(EmployeeError.EmployeeNotFound)
                    is IllegalArgumentException -> Err(EmployeeError.DuplicateVacationDates)
                    else -> Err(EmployeeError.UnexpectedError("An unexpected error occurred: ${e.message}"))
                }
            }
        )
    }
}
