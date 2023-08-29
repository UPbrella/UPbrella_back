package upbrella.be.slack.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import upbrella.be.config.SlackBotConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SlackAlarmServiceTest {
    @Mock
    private SlackBotConfig slackBotConfig;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SlackAlarmService slackAlarmService;

    @Test
    @DisplayName("우산을 반납하면 Slack 봇으로 잔여 환급 개수와 함께 알림이 전송된다.")
    void notifyReturn() {
        // given
        given(slackBotConfig.getWebHookUrl()).willReturn("https://hooks.slack.com/services");
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class))).willReturn(null);

        // when
        slackAlarmService.notifyReturn(1);

        // then
        Assertions.assertAll(
                () -> then(slackBotConfig).should(times(1))
                        .getWebHookUrl(),
                () -> then(restTemplate).should(times(1))
                        .exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)));
    }
}
