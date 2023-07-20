package upbrella.be.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import upbrella.be.login.dto.response.LoggedInUser;
import upbrella.be.login.dto.response.NaverLoginResponse;
import upbrella.be.login.dto.token.NaverOauthInfo;
import upbrella.be.login.dto.token.NaverToken;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OauthLoginService {

    private final NaverOauthInfo naverOauthInfo;

    public NaverToken getAccessToken(String code, String state) {

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

    public LoggedInUser processLogin(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        return new RestTemplate().exchange(
                        "https://openapi.naver.com/v1/nid/me",
                        HttpMethod.GET,
                        requestEntity,
                        NaverLoginResponse.class)
                .getBody()
                .getResponse();
    }

    // TODO: state 회의하기
    private void checkNaverState(String state) {

        if (state.equals(naverOauthInfo.getNaverState())) {
            // TODO: 401 커스텀 에러 만들기
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
    }
}