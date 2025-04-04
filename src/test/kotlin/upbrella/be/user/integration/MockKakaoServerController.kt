package upbrella.be.user.integration

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.OauthToken

@RestController
class MockKakaoServerController {

    @PostMapping("/oauth/token")
    fun getAccessToken(request: HttpEntity<String>): ResponseEntity<OauthToken> {
        val response = OauthToken(
            accessToken = "access_token",
            refreshToken = "refresh_token",
            tokenType = "bearer",
            expiresIn = 3600L
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping(path = ["/user/me"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getMemberInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) token: String): ResponseEntity<KakaoLoginResponse> {
        val response = KakaoLoginResponse(
            id = 1L,
            kakaoAccount = KakaoAccount(email = "email@email.com")
        )

        return ResponseEntity.ok(response)
    }
}