import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.error.EmployeeError
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.service.EmployeeService
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.multipart.MultipartFile


class EmployeeServiceTest {

    private val employeeRepository = mockk<EmployeeRepository>(relaxed = true)
    private val csvUtils = mockk<CsvUtils>()
    private lateinit var employeeService: EmployeeService

    @BeforeEach
    fun setUp() {
        employeeService = EmployeeService(employeeRepository, csvUtils)
    }

    @Test
    fun createEmployeeSuccessfully() {
        // Given
        val employeeDTO = EmployeeDTO(email = "user1@rbt.rs", password = "pdsadasdasda")
        every { employeeRepository.findByEmail(employeeDTO.email) } returns null
        every { employeeRepository.save(any<Employee>()) } returns Employee(
            email = employeeDTO.email,
            password = employeeDTO.password
        )

        // When
        val result = employeeService.createEmployee(employeeDTO)

        // Then
        assertThat(result).isEqualTo(Ok("Employee added"))
        verify { employeeRepository.save(any<Employee>()) }
    }

    @Test
    fun createEmployeeFailsWhenEmailAlreadyExists() {
        // Given
        val employeeDTO = EmployeeDTO(email = "user1@rbt.rs", password = "pdsadasdasda")
        every { employeeRepository.findByEmail(employeeDTO.email) } returns Employee(
            email = employeeDTO.email,
            password = "existingPassword"
        )

        // When
        val result = employeeService.createEmployee(employeeDTO)

        // Then
        assertThat(result).isEqualTo(Err(EmployeeError.DuplicateEmployee))
        verify(exactly = 0) { employeeRepository.save(any<Employee>()) }
    }

    @Test
    fun processAndSaveEmployeesSuccessfullyImportsEmployees() {
        // Given
        val file = mockk<MultipartFile>()
        val employees = listOf(
            Employee(email = "user1@rbt.rs", password = "hohohio"),
            Employee(email = "user2@rbt.rs", password = "siajodjaoi")
        )
        every { csvUtils.parseEmployees(file) } returns Ok(employees)
        every { employeeRepository.findByEmail(any()) } returns null
        every { employeeRepository.saveAll(employees) } returns employees

        // When
        val result = employeeService.processAndSaveEmployees(file)

        // Then
        assertThat(result).isEqualTo(Ok("Employees imported successfully."))
        verify { employeeRepository.saveAll(employees) }
    }


}
