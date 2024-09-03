import Project.Vacation.Tracker.dto.EmployeeDTO
import Project.Vacation.Tracker.error.EmployeeError
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.repository.EmployeeRepository
import Project.Vacation.Tracker.service.EmployeeService
import Project.Vacation.Tracker.utils.CsvUtils
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


class EmployeeServiceTest {

    private lateinit var employeeRepository: EmployeeRepository
    private lateinit var csvUtils: CsvUtils
    private lateinit var employeeService: EmployeeService

    @BeforeEach
    fun setUp() {
        employeeRepository = mock(EmployeeRepository::class.java)
        csvUtils = mock(CsvUtils::class.java)
        employeeService = EmployeeService(employeeRepository, csvUtils)
    }

    @Test
    fun `should create employee`() {
        // Given
        val employeeDTO = EmployeeDTO(email = "user1@rbt.rs", password = "pdsadasdasda")
        `when`(employeeRepository.findByEmail(employeeDTO.email)).thenReturn(null)
        `when`(employeeRepository.save(any(Employee::class.java))).thenReturn(
            Employee(email = employeeDTO.email, password = employeeDTO.password)
        )

        // When
        val result = employeeService.createEmployee(employeeDTO)

        // Then
        assertThat(result).isEqualTo(Ok("Employee added"))
        verify(employeeRepository).save(any(Employee::class.java))
    }

    @Test
    fun `create Employee Fails WhenEmail Already Exists`() {
        // Given
        val employeeDTO = EmployeeDTO(email = "user1@rbt.rs", password = "pdsadasdasda")
        `when`(employeeRepository.findByEmail(employeeDTO.email)).thenReturn(
            Employee(email = employeeDTO.email, password = "existingPassword")
        )


        // When
        val result = employeeService.createEmployee(employeeDTO)

        // Then
        assertThat(result).isEqualTo(Err(EmployeeError.DuplicateEmployee))
        verify(employeeRepository, never()).save(any(Employee::class.java))
    }

    @Test
    fun `process And save Employees Successfully Imports Employees`() {
        // Given
        val file = mock(MultipartFile::class.java)
        val employees = listOf(
            Employee(email = "user1@rbt.rs", password = "password1"),
            Employee(email = "user2@rbt.rs", password = "password2")
        )
        `when`(csvUtils.parseEmployees(file)).thenReturn(Ok(employees))
        `when`(employeeRepository.findByEmail(anyString())).thenReturn(null)
        `when`(employeeRepository.saveAll(employees)).thenReturn(employees)

        // When
        val result = employeeService.processAndSaveEmployees(file)

        // Then
        assertThat(result).isEqualTo(Ok("Employees imported successfully."))
        verify(employeeRepository).saveAll(employees)
    }

    @Test
    fun `process And save Employees Fails When Csv Parsing Fails`() {
        // Given
        val file = mock(MultipartFile::class.java)
        `when`(csvUtils.parseEmployees(file)).thenReturn(Err(EmployeeError.FileParseError("Invalid CSV format")))

        // When
        val result = employeeService.processAndSaveEmployees(file)

        // Then
        assertThat(result).isEqualTo(Err(EmployeeError.FileParseError("Failed to read or parse CSV file: Invalid CSV format")))
        verify(employeeRepository, never()).saveAll(anyList())
    }

    @Test
    fun `process and save Employees Fails When Data is Invalid`() {

        //Given
        val file = mock(MultipartFile::class.java)
        `when`(csvUtils.parseEmployees(file)).thenReturn(Err(EmployeeError.InvalidDataError("Invalid Data")))

        //When
        val result = employeeService.processAndSaveEmployees(file)

        //Then
        assertThat(result).isEqualTo(Err(EmployeeError.InvalidDataError("Invalid data in CSV file: Invalid Data")))
        verify(employeeRepository, never()).saveAll(anyList())
    }



    @Test
    fun `process And Save Employees Fails When Unexpected Error Occurs`() {
        // Given
        val file = mock(MultipartFile::class.java)
        `when`(csvUtils.parseEmployees(file)).thenReturn(Err(EmployeeError.UnexpectedError("An unknown error occurred")))

        // When
        val result = employeeService.processAndSaveEmployees(file)

        // Then
        assertThat(result).isEqualTo(Err(EmployeeError.UnexpectedError("An unknown error occurred")))
        verify(employeeRepository, never()).saveAll(anyList())
    }

}
