package upbrella.be.user.integration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import upbrella.be.user.dto.request.KakaoAccount;
import upbrella.be.user.dto.response.KakaoLoginResponse;
import upbrella.be.user.dto.token.OauthToken;

@RestController
public class MockKakaoServerController {

    @PostMapping(path = "/oauth/token")
    public ResponseEntity<OauthToken> getAccessToken(HttpEntity<String> request) {

        OauthToken response = OauthToken.builder()
                .accessToken("access_token")
                .refreshToken("refresh_token")
                .tokenType("bearer")
                .expiresIn(3600L)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/user/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KakaoLoginResponse> getMemberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        KakaoLoginResponse response = KakaoLoginResponse.builder()
                .id(1L)
                .kakaoAccount(KakaoAccount.builder()
                        .email("email@email.com")
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
