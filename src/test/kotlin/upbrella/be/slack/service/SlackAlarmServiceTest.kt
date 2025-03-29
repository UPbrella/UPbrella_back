package upbrella.be.slack.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import upbrella.be.config.SlackBotConfig

@ExtendWith(MockitoExtension::class)
class SlackAlarmServiceTest {
    @Mock
    private lateinit var slackBotConfig: SlackBotConfig

    @Mock
    private lateinit var restTemplate: RestTemplate

    @InjectMocks
    private lateinit var slackAlarmService: SlackAlarmService

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
        slackAlarmService.notifyReturn(1)

        // then
        Assertions.assertAll(
            {
                then(slackBotConfig).should(times(1))
                    .webHookUrl
            },
            {
                then(restTemplate).should(times(1))
                    .exchange(
                        anyString(),
                        any(HttpMethod::class.java),
                        any(),
                        any(Class::class.java)
                    )
            }
        )
    }
}