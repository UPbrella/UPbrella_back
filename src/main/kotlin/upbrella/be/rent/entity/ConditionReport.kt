package upbrella.be.rent.entity

import javax.persistence.*

@Entity
class ConditionReport(
    val historyId: Long,
    val content: String? = null,
    val etc: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
