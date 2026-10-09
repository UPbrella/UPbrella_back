package upbrella.be.user.dto.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import upbrella.be.user.entity.BlackList
import java.time.LocalDateTime

class SingleBlackListResponseTest {

    @Test
    @DisplayName("blockedAt은 DB에 저장된 한국 시간 그대로 응답한다")
    fun blockedAtShouldNotBeConverted() {
        // given
        val blackList = BlackList(
            socialId = 1L,
            blockedAt = LocalDateTime.of(2023, 7, 18, 9, 0, 0),
            id = 1L
        )

        // when
        val response = SingleBlackListResponse.of(blackList)

        // then
        assertThat(response.blockedAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("15시 이후 시간도 날짜가 다음 날로 바뀌지 않는다")
    fun afterThreePmShouldKeepDate() {
        // given
        val blackList = BlackList(
            socialId = 1L,
            blockedAt = LocalDateTime.of(2023, 7, 18, 23, 30, 0),
            id = 1L
        )

        // when
        val response = SingleBlackListResponse.of(blackList)

        // then
        assertThat(response.blockedAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 23, 30, 0))
    }
}
