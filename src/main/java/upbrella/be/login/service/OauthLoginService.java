package upbrella.be.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import upbrella.be.login.dto.response.KakaoLoginResponse;
import upbrella.be.login.dto.token.KakaoOauthInfo;
import upbrella.be.login.dto.token.OauthToken;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OauthLoginService {

    private final RestTemplate restTemplate;

    public OauthToken getOauthToken(String code, KakaoOauthInfo oauthInfo) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.setAll(header);

        MultiValueMap<String, String> requestPayloads = new LinkedMultiValueMap<>();
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("grant_type", "authorization_code");
        requestPayload.put("client_id", oauthInfo.getClientId());
        requestPayload.put("client_secret", oauthInfo.getClientSecret());
        requestPayload.put("code", code);
        requestPayloads.setAll(requestPayload);

        HttpEntity<?> request = new HttpEntity<>(requestPayloads, headers);
        ResponseEntity<OauthToken> response = restTemplate.postForEntity(oauthInfo.getRedirectUri(), request, OauthToken.class);

        return response.getBody();
    }

    private  <T> T processLogin(String accessToken, String loginUri, Class<T> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                        loginUri,
                        HttpMethod.GET,
                        requestEntity,
                        responseType)
                .getBody();
    }

    public KakaoLoginResponse processKakaoLogin(String accessToken, String loginUri) {
        KakaoLoginResponse response = processLogin(accessToken, loginUri, KakaoLoginResponse.class);
        return response;
    }
}
