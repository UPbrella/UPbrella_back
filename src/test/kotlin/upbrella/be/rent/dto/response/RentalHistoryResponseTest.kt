package upbrella.be.rent.dto.response

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RentalHistoryResponseTest {

    @Test
    @DisplayName("반납된 대여 내역의 rentAt은 DB에 저장된 한국 시간 그대로 응답한다")
    fun createReturnedHistoryRentAtShouldNotBeConverted() {
        // given
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 9, 0, 0),
            returnAt = LocalDateTime.of(2023, 7, 20, 9, 0, 0)
        )

        // when
        val response = RentalHistoryResponse.createReturnedHistory(history, 2, 2)

        // then
        assertThat(response.rentAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
    }

    @Test
    @DisplayName("반납된 대여 내역의 returnAt은 DB에 저장된 한국 시간 그대로 응답한다")
    fun createReturnedHistoryReturnAtShouldNotBeConverted() {
        // given
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 9, 0, 0),
            returnAt = LocalDateTime.of(2023, 7, 18, 15, 0, 0)
        )

        // when
        val response = RentalHistoryResponse.createReturnedHistory(history, 0, 0)

        // then
        assertThat(response.returnAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 15, 0, 0))
    }

    @Test
    @DisplayName("미반납 대여 내역의 rentAt은 DB에 저장된 한국 시간 그대로 응답한다")
    fun createNonReturnedHistoryRentAtShouldNotBeConverted() {
        // given
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2023, 7, 18, 9, 0, 0),
            returnAt = null
        )

        // when
        val response = RentalHistoryResponse.createNonReturnedHistory(history, 1)

        // then
        assertThat(response.rentAt).isEqualTo(LocalDateTime.of(2023, 7, 18, 9, 0, 0))
        assertThat(response.returnAt).isNull()
    }

    @Test
    @DisplayName("15시 이후에 대여해도 응답의 대여 날짜가 다음 날로 바뀌지 않는다")
    fun rentAfterThreePmShouldKeepDate() {
        // given
        // 10월 4일 대여가 어드민에 10월 5일로 보이던 문제 (#518)
        val history = createHistoryInfoDto(
            rentAt = LocalDateTime.of(2026, 10, 4, 18, 30, 0),
            returnAt = null
        )

        // when
        val response = RentalHistoryResponse.createNonReturnedHistory(history, 1)

        // then
        assertThat(response.rentAt).isEqualTo(LocalDateTime.of(2026, 10, 4, 18, 30, 0))
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
