package vacation.tracker.Employee.model

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = "employee")
data class Employee(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val Id : Long? = 0,
    @Column(unique = true)
    val email: String,
    val password : String,


    @JsonBackReference
    @OneToMany(mappedBy = "employee", cascade = [CascadeType.ALL])
    val vacation : List<Vacation>? = mutableListOf(),

    @JsonBackReference
    @OneToMany(mappedBy = "employee", cascade = [CascadeType.ALL])
    val vacationDates: List<Vacation>?= mutableListOf()

)