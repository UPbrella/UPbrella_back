package upbrella.be.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import upbrella.be.config.FixtureFactory;
import upbrella.be.user.dto.request.KakaoAccount;
import upbrella.be.user.dto.response.KakaoLoginResponse;
import upbrella.be.user.dto.token.KakaoOauthInfo;
import upbrella.be.user.dto.token.OauthToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OauthLoginServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private KakaoOauthInfo kakaoOauthInfo;
    @InjectMocks
    private OauthLoginService oauthLoginService;

    @Test
    @DisplayName("사용자가 승인한 코드를 카카오 API 인증 서버에 전송해 인증 토큰을 받을 수 있다.")
    void getOauthTokenTest() {

        //given
        String code = "{\"code\":\"1kdfjq0243f\"}";
        OauthToken token = FixtureFactory.buildOauthToken();
        given(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                .willReturn(ResponseEntity.of(Optional.of(token)));
        kakaoOauthInfo = new KakaoOauthInfo("123", "abc", "kakao.com", "login.com");

        //when
        OauthToken actualToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo);

        //then
        assertAll(() -> assertThat(actualToken)
                        .usingRecursiveComparison()
                        .isEqualTo(token),
                () -> then(restTemplate).should(times(1))
                        .postForEntity(anyString(), any(HttpEntity.class), any()));
    }

    @Test
    @DisplayName("사용자는 인증 토큰을 카카오 API Resource 서버에 전송해 사용자 정보를 받을 수 있다.")
    void processKakaoLoginTest() {

        //given
        String oauthToken = "abc";
        String loginUri = "login.com";
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)))
                .willReturn(ResponseEntity.of(Optional.of(new KakaoLoginResponse(3L, KakaoAccount.builder().build()))));

        // when
        KakaoLoginResponse kakaoLoginResponse = oauthLoginService.processKakaoLogin(oauthToken, loginUri);

        // then
        assertAll(() -> assertThat(kakaoLoginResponse.getId())
                .isEqualTo(3L),
                () -> then(restTemplate).should(times(1))
                        .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class)));
    }
}
