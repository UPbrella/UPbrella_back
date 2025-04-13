package upbrella.be.rent.entity

import javax.persistence.*

@Entity
class ImprovementReport(
    @OneToOne
    @JoinColumn(name = "history_id")
    val history: History,
    val content: String? = null,
    val etc: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
