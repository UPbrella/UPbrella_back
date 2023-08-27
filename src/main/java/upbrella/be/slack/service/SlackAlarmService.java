package upbrella.be.slack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest;
import upbrella.be.user.entity.User;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpMethod.POST;

@Service
@RequiredArgsConstructor
public class SlackAlarmService {

    private ObjectMapper objectMapper = new ObjectMapper();
    public void notifyReturn(User userToReturn, ReturnUmbrellaByUserRequest returnUmbrellaByUserRequest) throws JsonProcessingException {

        StringBuilder sb = new StringBuilder();
        sb.append("--- 반납 유저 정보 ---\n")
                .append("```")
                .append(objectMapper.writeValueAsString(userToReturn))
                .append("```")
                .append("\n--- 반납 우산 정보 ---\n")
                .append("```")
                .append(objectMapper.writeValueAsString(returnUmbrellaByUserRequest))
                .append("```");
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
