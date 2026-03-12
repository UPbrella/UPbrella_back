package upbrella.be.user.dto.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.entity.User
import java.time.LocalDateTime

class SingleHistoryResponseTest {

    private lateinit var history: History

    @BeforeEach
    fun setUp() {
        val storeMeta = StoreMeta(
            id = 1L,
            name = "test store",
            deleted = false,
            category = "category",
            activated = false
        )

        val umbrella = Umbrella(
            id = 1L,
            uuid = 100L,
            deleted = false,
            storeMeta = storeMeta,
            rentable = true,
            createdAt = LocalDateTime.now(),
            etc = "etc",
            missed = false,
        )

        val user = User(
            socialId = 1L,
            name = "tester",
            phoneNumber = "010-1234-5678",
            email = "email",
            provider = "KAKAO",
            adminStatus = false,
            bank = null,
            accountNumber = null,
            id = 1L
        )

        history = History(
            id = 1L,
            rentedAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
            returnedAt = LocalDateTime.of(2023, 7, 20, 0, 0, 0),
            umbrella = umbrella,
            user = user,
            rentStoreMeta = storeMeta,
        )
    }

    @Test
    @DisplayName("rentedAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun rentedAtShouldBeConvertedToKst() {
        // given
        // UTC 2023-07-18 00:00:00 -> KST 2023-07-18 09:00:00
        val returnAt = LocalDateTime.of(2023, 7, 20, 0, 0, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.rentedAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("returnAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun returnAtShouldBeConvertedToKst() {
        // given
        // UTC 2023-07-18 15:00:00 -> KST 2023-07-19 00:00:00
        val returnAt = LocalDateTime.of(2023, 7, 18, 15, 0, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 19, 0, 0, 0))
    }

    @Test
    @DisplayName("UTC 자정 직전 시간은 KST로 변환 시 날짜가 변경되어야 한다")
    fun midnightBoundaryShouldChangeDate() {
        // given
        // UTC 2023-07-18 23:30:00 -> KST 2023-07-19 08:30:00
        val returnAt = LocalDateTime.of(2023, 7, 18, 23, 30, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 19, 8, 30, 0))
    }
}
