package upbrella.be.user.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import upbrella.be.rent.entity.History

data class SingleHistoryResponse(
    val umbrellaUuid: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    val rentedAt: LocalDateTime,
    val rentedStore: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    val returnAt: LocalDateTime,
    val isReturned: Boolean,
    val isRefunded: Boolean
) {
    companion object {
        fun ofUserHistory(history: History, returnAt: LocalDateTime, isReturned: Boolean, isRefunded: Boolean): SingleHistoryResponse {
            return SingleHistoryResponse(
                umbrellaUuid = history.umbrella.uuid,
                rentedAt = history.rentedAt,
                rentedStore = history.rentStoreMeta.name,
                returnAt = returnAt,
                isReturned = isReturned,
                isRefunded = isRefunded
            )
        }
    }
}