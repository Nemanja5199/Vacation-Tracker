package vacation.tracker.Employee.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*

@Entity
@Table( name = "vacations")
data class Vacation (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id: Long? = 0,
    val vacationDays : Int,
    val vacationYear : Int,




    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "email" , referencedColumnName = "email")
    val employee: Employee,

    )
