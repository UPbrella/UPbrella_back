package upbrella.be.user.service

import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.token.KakaoOauthInfo
import upbrella.be.user.dto.token.OauthToken

@Service
class OauthLoginService(
    private val restTemplate: RestTemplate
) {

    fun getOauthToken(code: String, oauthInfo: KakaoOauthInfo): OauthToken? {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            setAll(
                mapOf(
                    "Accept" to "application/json",
                    "Content-Type" to "application/x-www-form-urlencoded;charset=utf-8"
                )
            )
        }

        val requestPayloads: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            setAll(
                mapOf(
                    "grant_type" to "authorization_code",
                    "client_id" to oauthInfo.clientId,
                    "client_secret" to oauthInfo.clientSecret,
                    "code" to code
                )
            )
        }

        val request = HttpEntity(requestPayloads, headers)
        val response = restTemplate.postForEntity(oauthInfo.redirectUri, request, OauthToken::class.java)

        return response.body
    }

    private fun <T> processLogin(accessToken: String, loginUri: String, responseType: Class<T>): T? {
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $accessToken")
            contentType = MediaType.APPLICATION_JSON
        }

        val requestEntity = HttpEntity<Any>(headers)

        val response = restTemplate.exchange(
            loginUri,
            HttpMethod.GET,
            requestEntity,
            responseType
        )

        return response.body
    }

    fun processKakaoLogin(accessToken: String, loginUri: String): KakaoLoginResponse? {
        return processLogin(accessToken, loginUri, KakaoLoginResponse::class.java)
    }
}
