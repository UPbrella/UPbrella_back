package upbrella.be.user.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.time.ZoneId
import upbrella.be.rent.entity.History

data class SingleHistoryResponse(
    val umbrellaUuid: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val rentedAt: LocalDateTime,
    val rentedStore: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val returnAt: LocalDateTime,
    val isReturned: Boolean,
    val isRefunded: Boolean
) {
    companion object {

        private val KST = ZoneId.of("Asia/Seoul")

        private fun toKst(time: LocalDateTime): LocalDateTime {
            return time.atZone(ZoneId.of("UTC")).withZoneSameInstant(KST).toLocalDateTime()
        }

        fun ofUserHistory(history: History, returnAt: LocalDateTime, isReturned: Boolean, isRefunded: Boolean): SingleHistoryResponse {
            return SingleHistoryResponse(
                umbrellaUuid = history.umbrella.uuid,
                rentedAt = toKst(history.rentedAt),
                rentedStore = history.rentStoreMeta.name,
                returnAt = toKst(returnAt),
                isReturned = isReturned,
                isRefunded = isRefunded
            )
        }
    }
}