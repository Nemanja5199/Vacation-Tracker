package Project.Vacation.Tracker.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import java.time.LocalDate


@Entity
data class VacationDates(


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id: Long? = 0,

    val startDate: LocalDate,

    val endDate: LocalDate,


    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "email", referencedColumnName = "email")
    val employee: Employee,


    )
