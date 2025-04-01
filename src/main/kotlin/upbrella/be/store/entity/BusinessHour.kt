package upbrella.be.store.entity

import upbrella.be.store.dto.request.SingleBusinessHourRequest
import java.time.DayOfWeek
import java.time.LocalTime
import javax.persistence.*

@Entity
class BusinessHour(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_meta_id")
    val storeMeta: StoreMeta? = null,
    @Enumerated(EnumType.STRING)
    val date: DayOfWeek,
    val openAt: LocalTime? = null,
    val closeAt: LocalTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    companion object {
        @JvmStatic
        fun ofCreateBusinessHour(
            request: SingleBusinessHourRequest,
            storeMeta: StoreMeta
        ): BusinessHour {
            return BusinessHour(
                storeMeta = storeMeta,
                date = request.date,
                openAt = request.openAt,
                closeAt = request.closeAt
            )
        }
    }
}