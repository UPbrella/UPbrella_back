package upbrella.be.rent.dto.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class RentalHistoryResponseTest {

    private lateinit var originalTimeZone: TimeZone

    @BeforeEach
    fun setUp() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @AfterEach
    fun tearDown() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    @DisplayName("반납된 대여 내역의 rentAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun createReturnedHistoryRentAtShouldBeKst() {
        // given
        // UTC 2023-07-18 00:00:00 -> KST 2023-07-18 09:00:00
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
            returnAt = LocalDateTime.of(2023, 7, 20, 0, 0, 0)
        )

        // when
        val response = RentalHistoryResponse.createReturnedHistory(history, 2, 2)

        // then
        assertThat(response.rentAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("반납된 대여 내역의 returnAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun createReturnedHistoryReturnAtShouldBeKst() {
        // given
        // UTC 2023-07-18 15:00:00 -> KST 2023-07-19 00:00:00
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
            returnAt = LocalDateTime.of(2023, 7, 18, 15, 0, 0)
        )

        // when
        val response = RentalHistoryResponse.createReturnedHistory(history, 1, 1)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 19, 0, 0, 0))
    }

    @Test
    @DisplayName("미반납 대여 내역의 rentAt은 UTC에서 KST(+9시간)로 변환되어야 한다")
    fun createNonReturnedHistoryRentAtShouldBeKst() {
        // given
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 0, 0, 0),
            returnAt = null
        )

        // when
        val response = RentalHistoryResponse.createNonReturnedHistory(history, 1)

        // then
        assertThat(response.rentAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
        assertThat(response.returnAt).isNull()
    }

    private fun createHistoryInfoDto(
        rentAt: LocalDateTime,
        returnAt: LocalDateTime?
    ): HistoryInfoDto {
        return HistoryInfoDto(
            id = 1L,
            name = "테스터",
            phoneNumber = "010-1234-5678",
            rentStoreName = "대여점",
            rentAt = rentAt,
            umbrellaUuid = 1L,
            returnStoreName = returnAt?.let { "반납점" },
            returnAt = returnAt,
            paidAt = null,
            bank = null,
            accountNumber = null,
            etc = null,
            refundedAt = null
        )
    }
}
