package Project.Vacation.Tracker.utils

import Project.Vacation.Tracker.exeption.EmployeeExistsException
import Project.Vacation.Tracker.model.Employee
import Project.Vacation.Tracker.model.Vacation
import Project.Vacation.Tracker.model.VacationDates
import Project.Vacation.Tracker.repository.EmployeeRepository
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVFormat
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Component
class CsvUtils(private val employeeRepository: EmployeeRepository) {

/*
    public  fun parseEmployees(file: MultipartFile): List<Employee> {
        val employees = mutableListOf<Employee>()

        file.inputStream.bufferedReader().use { reader ->
            val lines = reader.lines().toList()

            for (line in lines.drop(2)) { // Skip header
                val columns = line.split(",")
                if (columns.size == 2) {
                    val email = columns[0].trim()
                    val password = columns[1].trim()
                    val employee = Employee(email = email, password = password)
                    employees.add(employee)
                }
            }
        }

        return employees
    }*/


    fun parseEmployees(file: MultipartFile): List<Employee> {
        val employees = mutableListOf<Employee>()

        file.inputStream.bufferedReader().use { reader ->

            val lines = reader.readLines().drop(1)
            val csvParser = CSVParser.parse(lines.joinToString("\n"),
                CSVFormat.RFC4180.withFirstRecordAsHeader())

            for (record in csvParser) {
                val email = record.get("Employee Email").trim()
                val password = record.get("Employee Password").trim()
                val employee = Employee(email = email, password = password)
                employees.add(employee)
            }
        }

        return employees
    }


    fun parseVacations(file :MultipartFile):List<Vacation>{
        val vacations = mutableListOf<Vacation>()
        val vacationYear : Int



        file.inputStream.bufferedReader().use { reader ->

            val line = reader.readLine()
            val columns = line.split(",")
            vacationYear= columns[1].trim().toInt()

            val lines = reader.readLines()
            val csvParser = CSVParser.parse(
                lines.joinToString("\n"),
                CSVFormat.RFC4180.withFirstRecordAsHeader()
            )



            for (record in csvParser){

                val email = record.get("Employee").trim()
                val days = record.get("Total vacation days").trim().toInt()

                if(employeeRepository.existsByEmail(email)){

                    val employee = employeeRepository.findByEmail(email).get()

                    val vacation = Vacation(employee = employee, vacationYear = vacationYear, vacationDays = days )
                    vacations.add(vacation)
                }

                else
                    throw EmployeeExistsException("Employee not found in vacations")

            }
            return vacations
        }


    }



    fun parseVacationDates(file :MultipartFile):List<VacationDates>{


        val  dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)


        file.inputStream.bufferedReader().use { reader ->

            val vacationsDates = mutableListOf<VacationDates>()
            val lines= reader.readLines()

            val csvParser = CSVParser.parse(lines.joinToString("\n"),
            CSVFormat.RFC4180.withFirstRecordAsHeader())


            for (record in csvParser){

                val email = record.get("Employee").trim()
                val startDate = LocalDate.parse(record.get("Vacation start date").trim(), dateFormatter)
                val endDate = LocalDate.parse(record.get("Vacation end date").trim(), dateFormatter)


                val employee = employeeRepository.findByEmail(email).orElseThrow{ EmployeeExistsException(" Employee not found") }
                val vacationDate = VacationDates(employee = employee , startDate = startDate , endDate = endDate)

                vacationsDates.add(vacationDate)
            }

            return vacationsDates



        }


    }





}