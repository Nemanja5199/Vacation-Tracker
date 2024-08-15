package Project.Vacation.Tracker

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class VacationTrackerApplication

fun main(args: Array<String>) {
	runApplication<VacationTrackerApplication>(*args)
}
