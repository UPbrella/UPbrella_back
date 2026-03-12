package upbrella.be.user.dto.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import upbrella.be.user.entity.BlackList
import java.time.LocalDateTime

class SingleBlackListResponseTest {

    @Test
    @DisplayName("blockedAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun blockedAtShouldBeConvertedToKst() {
        // given
        // UTC 2023-07-18 00:00:00 -> KST 2023-07-18 09:00:00
        val blackList = BlackList(
            socialId = 1L,
            blockedAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
            id = 1L
        )

        // when
        val response = SingleBlackListResponse.of(blackList)

        // then
        assertThat(response.blockedAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("UTC 자정 직전 시간은 KST로 변환 시 날짜가 변경되어야 한다")
    fun midnightBoundaryShouldChangeDate() {
        // given
        // UTC 2023-07-18 23:30:00 -> KST 2023-07-19 08:30:00
        val blackList = BlackList(
            socialId = 1L,
            blockedAt = LocalDateTime.of(2023, 7, 18, 23, 30, 0),
            id = 1L
        )

        // when
        val response = SingleBlackListResponse.of(blackList)

        // then
        assertThat(response.blockedAt).isEqualTo(LocalDateTime.of(2023, 7, 19, 8, 30, 0))
    }
}
