package upbrella.be.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import upbrella.be.login.dto.token.NaverOauthInfo;
import upbrella.be.login.dto.token.NaverToken;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OauthLoginService {

    private final NaverOauthInfo naverOauthInfo;

    public NaverToken getAccessToken(String code, String state) {
        checkNaverState(state);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        headers.setAll(header);

        MultiValueMap<String, String> requestPayloads = new LinkedMultiValueMap<>();
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("client_id", naverOauthInfo.getNaverClientId());
        requestPayload.put("client_secret", naverOauthInfo.getNaverClientSecret());
        requestPayload.put("state", naverOauthInfo.getNaverState());
        requestPayload.put("code", code);
        requestPayload.put("grant_type", "authorization_code");
        requestPayloads.setAll(requestPayload);

        HttpEntity<?> request = new HttpEntity<>(requestPayloads, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverToken> response = restTemplate.postForEntity(naverOauthInfo.getNaverUrl(), request, NaverToken.class);

        return response.getBody();
    }

    private void checkNaverState(String state) {
        if (state != naverOauthInfo.getNaverState()) {
            // TODO: 401 커스텀 에러 만들기
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}
