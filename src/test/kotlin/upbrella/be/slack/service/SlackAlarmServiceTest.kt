package upbrella.be.slack.service

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import upbrella.be.config.SlackBotConfig
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.user.entity.User
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class SlackAlarmServiceTest {
    @Mock
    private lateinit var slackBotConfig: SlackBotConfig

    @Mock
    private lateinit var restTemplate: RestTemplate

    @InjectMocks
    private lateinit var slackAlarmService: SlackAlarmService

    private lateinit var rentUmbrellaByUserRequest: RentUmbrellaByUserRequest
    private lateinit var foundStoreMeta: StoreMeta
    private lateinit var foundUmbrella: Umbrella
    private lateinit var userToRent: User
    private lateinit var history: History

    @BeforeEach
    fun setUp() {
        rentUmbrellaByUserRequest = RentUmbrellaByUserRequest(
            region = "신촌",
            storeId = 25L,
            umbrellaId = 99L,
            conditionReport = "상태 양호"
        )

        foundStoreMeta = StoreMeta(
            id = 25L,
            name = "motive study cafe",
            deleted = false,
            category = "category",
            activated = false
        )

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

        userToRent = User(
            0L,
            "테스터",
            "010-1234-5678",
            "email",
            false,
            null,
            null,
            11L
        )

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
    }

    @Test
    @DisplayName("우산을 반납하면 Slack 봇으로 잔여 환급 개수와 함께 알림이 전송된다.")
    fun notifyReturn() {
        // given
        given(slackBotConfig.webHookUrl).willReturn("https://hooks.slack.com/services")
        given(
            restTemplate.exchange(
                anyString(),
                any(HttpMethod::class.java),
                any(),
                any(Class::class.java)
            )
        ).willReturn(null)

        // when
        slackAlarmService.notifyReturn(userToRent, history, 1L)

        val requestEntityCaptor =
            ArgumentCaptor.forClass(HttpEntity::class.java as Class<HttpEntity<Any>>)
        // then

        assertAll(
            {
                then(restTemplate).should(times(1))
                    .exchange(
                        anyString(),
                        any(HttpMethod::class.java),
                        requestEntityCaptor.capture(),
                        any(Class::class.java)
                    )
            },
            {
                assertTrue(
                    requestEntityCaptor.value.body.toString().contains("우산 반납 알림")
                )
            }
        )
    }
}
