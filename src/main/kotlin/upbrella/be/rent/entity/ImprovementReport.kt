package upbrella.be.rent.entity

import javax.persistence.*

@Entity
class ImprovementReport(
    @OneToOne
    @JoinColumn(name = "history_id")
    val history: History? = null,
    val content: String? = null,
    val etc: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? =null
) {
    companion object {
        @JvmStatic
        fun createFromReturn(history: History, content: String): ImprovementReport {
            return ImprovementReport(
                history = history,
                content = content,
            )
        }
    }
}