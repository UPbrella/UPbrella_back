package upbrella.be.rent.dto.response

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class HistoryInfoDto @QueryProjection constructor(
    val id: Long,
    val name: String,
    val phoneNumber: String?,
    val rentStoreName: String,
    val rentAt: LocalDateTime,
    val umbrellaUuid: Long,
    val returnStoreName: String?,
    val returnAt: LocalDateTime?,
    val paidAt: LocalDateTime?,
    val bank: String?,
    val accountNumber: String?,
    val etc: String?,
    val refundedAt: LocalDateTime?
)
