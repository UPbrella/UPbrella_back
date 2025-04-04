package upbrella.be.user.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import upbrella.be.config.FixtureFactory
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.KakaoOauthInfo
import java.util.*

@ExtendWith(MockitoExtension::class)
class OauthLoginServiceTest {

    @Mock
    private lateinit var restTemplate: RestTemplate

    private lateinit var kakaoOauthInfo: KakaoOauthInfo

    @InjectMocks
    private lateinit var oauthLoginService: OauthLoginService

    @Test
    @DisplayName("사용자가 승인한 코드를 카카오 API 인증 서버에 전송해 인증 토큰을 받을 수 있다.")
    fun getOauthTokenTest() {
        // given
        val code = """{"code":"1kdfjq0243f"}"""
        val token = FixtureFactory.buildOauthToken()

        given(restTemplate.postForEntity(anyString(), any(HttpEntity::class.java), any<Class<*>>()))
            .willReturn(ResponseEntity.of(Optional.of(token)))

        kakaoOauthInfo = KakaoOauthInfo("123", "abc", "kakao.com", "login.com")

        // when
        val actualToken = oauthLoginService.getOauthToken(code, kakaoOauthInfo)

        // then
        assertAll(
            { assertThat(actualToken).usingRecursiveComparison().isEqualTo(token) },
            {
                then(restTemplate).should(times(1))
                    .postForEntity(anyString(), any(HttpEntity::class.java), any<Class<*>>())
            }
        )
    }

    @Test
    @DisplayName("사용자는 인증 토큰을 카카오 API Resource 서버에 전송해 사용자 정보를 받을 수 있다.")
    fun processKakaoLoginTest() {
        // given
        val oauthToken = "abc"
        val loginUri = "login.com"
        val response = KakaoLoginResponse(3L, KakaoAccount.builder().build())

        given(
            restTemplate.exchange(
                anyString(),
                any(HttpMethod::class.java),
                any(HttpEntity::class.java),
                any<Class<*>>()
            )
        ).willReturn(ResponseEntity.of(Optional.of(response)))

        // when
        val kakaoLoginResponse = oauthLoginService.processKakaoLogin(oauthToken, loginUri)

        // then
        assertAll(
            { assertThat(kakaoLoginResponse!!.id).isEqualTo(3L) },
            {
                then(restTemplate).should(times(1))
                    .exchange(
                        anyString(),
                        any(HttpMethod::class.java),
                        any(HttpEntity::class.java),
                        any<Class<*>>()
                    )
            }
        )
    }
}
