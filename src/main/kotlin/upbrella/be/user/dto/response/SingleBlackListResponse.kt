package upbrella.be.user.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import upbrella.be.user.entity.BlackList

data class SingleBlackListResponse(
    val id: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss")
    val blockedAt: LocalDateTime
) {
    companion object {
        fun of(blackList: BlackList): SingleBlackListResponse {
            return SingleBlackListResponse(
                id = blackList.id!!,
                blockedAt = blackList.blockedAt
            )
        }
    }
}
