package upbrella.be.slack.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@Service
@RequiredArgsConstructor
public class SlackAlarmService {

    public void notifyReturn(long unrefundedCount) {

        StringBuilder sb = new StringBuilder();
        sb.append("*우산이 반납되었습니다. 보증금을 환급해주세요.*\n\n")
                .append("*환급 대기 건수* : ")
                .append(unrefundedCount);

        send(sb.toString());
    }

    private void send(String message) {

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> request = new HashMap<>();
        request.put("text", message);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request);

        String url = "https://hooks.slack.com/services/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";

        restTemplate.exchange(url, POST, entity, String.class);
    }
}
