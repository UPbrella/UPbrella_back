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
            rentedAt = LocalDateTime.of(2023, 7, 18, 9, 0, 0),
            returnedAt = LocalDateTime.of(2023, 7, 20, 9, 0, 0),
            umbrella = umbrella,
            user = user,
            rentStoreMeta = storeMeta,
        )
    }

    @Test
    @DisplayName("rentedAt은 DB에 저장된 한국 시간 그대로 응답한다")
    fun rentedAtShouldNotBeConverted() {
        // given
        val returnAt = LocalDateTime.of(2023, 7, 20, 9, 0, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.rentedAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("returnAt은 전달받은 한국 시간 그대로 응답한다")
    fun returnAtShouldNotBeConverted() {
        // given
        val returnAt = LocalDateTime.of(2023, 7, 18, 15, 0, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 15, 0, 0))
    }

    @Test
    @DisplayName("15시 이후 시간도 날짜가 다음 날로 바뀌지 않는다")
    fun afterThreePmShouldKeepDate() {
        // given
        val returnAt = LocalDateTime.of(2023, 7, 18, 23, 30, 0)

        // when
        val response = SingleHistoryResponse.ofUserHistory(history, returnAt, true, false)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 23, 30, 0))
    }
}
