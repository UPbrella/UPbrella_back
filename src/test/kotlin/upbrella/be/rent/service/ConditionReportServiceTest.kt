package upbrella.be.rent.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.rent.dto.response.ConditionReportPageResponse
import upbrella.be.rent.dto.response.ConditionReportResponse
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.entity.History
import upbrella.be.rent.repository.ConditionReportRepository
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.entity.User
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ConditionReportServiceTest {

    @Mock
    private lateinit var conditionReportRepository: ConditionReportRepository

    @InjectMocks
    private lateinit var conditionReportService: ConditionReportService

    private lateinit var foundStoreMeta: StoreMeta
    private lateinit var foundUmbrella: Umbrella
    private lateinit var userToRent: User
    private lateinit var history: History
    private lateinit var conditionReport: ConditionReport

    @BeforeEach
    fun setUp() {
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

        conditionReport = ConditionReport.builder()
            .id(1L)
            .content("content")
            .history(history)
            .etc("etc")
            .build()
    }

    @Nested
    @DisplayName("사용자(관리자)는 상태 신고 내역을 조회할 수 있다.")
    inner class FindConditionReportsTest {

        @Test
        @DisplayName("사용자(관리자)는 상태 신고 내역 조회를 할 수 있다.")
        fun success() {
            // given
            val conditionReportsResponse = ConditionReportPageResponse.builder()
                .conditionReports(
                    listOf(
                        ConditionReportResponse.builder()
                            .id(33L)
                            .umbrellaUuid(99L)
                            .content("content")
                            .etc("etc")
                            .build()
                    )
                ).build()

            given(conditionReportRepository.findAll())
                .willReturn(listOf(conditionReport))

            // when
            val allConditionReports = conditionReportService.findAll()

            // then
            assertAll(
                {
                    assertThat(allConditionReports)
                        .usingRecursiveComparison()
                        .isEqualTo(conditionReportsResponse)
                },
                {
                    assertThat(allConditionReports.conditionReports.size).isEqualTo(1)
                },
                {
                    then(conditionReportRepository).should(times(1)).findAll()
                }
            )
        }
    }
}
