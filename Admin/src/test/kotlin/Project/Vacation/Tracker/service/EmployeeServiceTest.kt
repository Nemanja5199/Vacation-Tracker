import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.service.EmployeeService
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.Ok
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val employeeDTO = EmployeeDTO(email = "user101@rbt.rs", password = "pdsadasdasda")
        every { employeeRepository.findByEmail(employeeDTO.email) } returns null
        every { employeeRepository.save(any<Employee>()) } returns Employee(email = employeeDTO.email, password = employeeDTO.password)

        // When
        val result = employeeService.createEmployee(employeeDTO)

        // Then
        assertThat(result).isEqualTo(Ok("Employee added"))
        verify { employeeRepository.save(any<Employee>()) }
    }
}
