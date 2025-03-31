package upbrella.be.rent.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.dto.response.SingleHistoryResponse
import java.time.LocalDateTime
import upbrella.be.user.entity.User
import org.junit.jupiter.api.Assertions.assertAll

class JavaHistoryTest {

    private lateinit var rentUmbrellaByUserRequest: RentUmbrellaByUserRequest
    private lateinit var foundStoreMeta: StoreMeta
    private lateinit var foundUmbrella: Umbrella
    private lateinit var userToRent: User
    private lateinit var history: History

    @BeforeEach
    fun setUp() {
        rentUmbrellaByUserRequest = RentUmbrellaByUserRequest.builder()
            .region("신촌")
            .storeId(25L)
            .umbrellaId(99L)
            .conditionReport("상태 양호")
            .build()

        foundStoreMeta = StoreMeta.builder()
            .id(25L)
            .name("motive study cafe")
            .deleted(false)
            .build()

        foundUmbrella = Umbrella(
            id = 99L,
            uuid = 99L,
            deleted = false,
            storeMeta = foundStoreMeta,
            rentable = true,
            createdAt = LocalDateTime.now(),
            etc = "etc",
            missed = false,
        )

        userToRent = User(1L, "테스터", "010-1234-5678", "email", false, null, null, 11L)
    }

    @Test
    @DisplayName("반납 날짜가 없으면 미반납, 반납일은 대여일+7일로 설정")
    fun notReturnedTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            refundedBy = userToRent,
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        val expectedResponse = SingleHistoryResponse.builder()
            .umbrellaUuid(99L)
            .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
            .returnAt(LocalDateTime.of(1000, 12, 3, 4, 24).plusDays(7))
            .rentedStore("motive study cafe")
            .isRefunded(false)
            .isReturned(false)
            .build()

        // when
        val singleHistoryResponse = History.ofUserHistory(history)

        // then
        assertThat(singleHistoryResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse)
    }

    @Test
    @DisplayName("반납 날짜가 존재하면 반납 처리, 반납 일시 그대로 표시")
    fun returnedTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            refundedAt = null,
            refundedBy = userToRent,
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        val expectedResponse = SingleHistoryResponse.builder()
            .umbrellaUuid(99L)
            .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
            .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
            .rentedStore("motive study cafe")
            .isRefunded(false)
            .isReturned(true)
            .build()

        // when
        val singleHistoryResponse = History.ofUserHistory(history)

        // then
        assertThat(singleHistoryResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse)
    }

    @Test
    @DisplayName("환급 날짜가 없으면 미환급 처리")
    fun notRefundedTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            refundedAt = null,
            refundedBy = userToRent,
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        val expectedResponse = SingleHistoryResponse.builder()
            .umbrellaUuid(99L)
            .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
            .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
            .rentedStore("motive study cafe")
            .isRefunded(false)
            .isReturned(true)
            .build()

        // when
        val singleHistoryResponse = History.ofUserHistory(history)

        // then
        assertThat(singleHistoryResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse)
    }

    @Test
    @DisplayName("환급 날짜가 있으면 환급 처리")
    fun refundedTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            refundedAt = LocalDateTime.of(1000, 12, 3, 4, 26),
            refundedBy = userToRent,
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        val expectedResponse = SingleHistoryResponse.builder()
            .umbrellaUuid(99L)
            .rentedAt(LocalDateTime.of(1000, 12, 3, 4, 24))
            .returnAt(LocalDateTime.of(1000, 12, 3, 4, 25))
            .rentedStore("motive study cafe")
            .isRefunded(true)
            .isReturned(true)
            .build()

        // when
        val singleHistoryResponse = History.ofUserHistory(history)

        // then
        assertThat(singleHistoryResponse)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse)
    }

    @Test
    @DisplayName("환급 처리할 유저, 환급 처리 시각 받아 대여 내역 환급 확인")
    fun refundTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        // when
        history.refund(userToRent, LocalDateTime.of(1000, 1, 2, 3, 4, 5))

        // then
        assertAll(
            { assertThat(history.refundedBy).isEqualTo(userToRent) },
            { assertThat(history.refundedAt).isEqualTo(LocalDateTime.of(1000, 1, 2, 3, 4, 5)) },
        )
    }

    @Test
    @DisplayName("지불 처리할 유저, 처리 시각 받아 대여 내역 지불 확인")
    fun paidTest() {
        // given
        history = History(
            id = 33L,
            rentedAt = LocalDateTime.of(1000, 12, 3, 4, 24),
            returnedAt = LocalDateTime.of(1000, 12, 3, 4, 25),
            returnStoreMeta = foundStoreMeta,
            umbrella = foundUmbrella,
            user = userToRent,
            rentStoreMeta = foundStoreMeta,
        )

        // when
        history.paid(userToRent, LocalDateTime.of(1000, 1, 2, 3, 4, 5))

        // then
        assertAll(
            { assertThat(history.paidBy).isEqualTo(userToRent) },
            { assertThat(history.paidAt).isEqualTo(LocalDateTime.of(1000, 1, 2, 3, 4, 5)) },
        )
    }
}
