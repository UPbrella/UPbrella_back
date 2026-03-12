package upbrella.be.user.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.time.ZoneId
import upbrella.be.user.entity.BlackList

data class SingleBlackListResponse(
    val id: Long,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val blockedAt: LocalDateTime
) {
    companion object {

        private val KST = ZoneId.of("Asia/Seoul")

        private fun toKst(time: LocalDateTime): LocalDateTime {
            return time.atZone(ZoneId.of("UTC")).withZoneSameInstant(KST).toLocalDateTime()
        }

        fun of(blackList: BlackList): SingleBlackListResponse {
            return SingleBlackListResponse(
                id = blackList.id!!,
                blockedAt = toKst(blackList.blockedAt)
            )
        }
    }
}
