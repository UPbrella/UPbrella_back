package upbrella.be.slack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upbrella.be.config.SlackBotConfig;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@Service
@RequiredArgsConstructor
public class SlackAlarmService {
    private final SlackBotConfig slackBotConfig;
    private final RestTemplate restTemplate;

    public void notifyReturn(long unrefundedCount) {

        StringBuilder sb = new StringBuilder();
        sb.append("*우산이 반납되었습니다. 보증금을 환급해주세요.*\n\n")
                .append("*환급 대기 건수* : ")
                .append(unrefundedCount);

        send(sb.toString());
    }

    private void send(String message) {

        Map<String, Object> request = new HashMap<>();
        request.put("text", message);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request);

        restTemplate.exchange(slackBotConfig.getWebHookUrl(), POST, entity, String.class);
    }
}
