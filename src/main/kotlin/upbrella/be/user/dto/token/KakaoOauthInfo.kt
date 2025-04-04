package upbrella.be.user.dto.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KakaoOauthInfo(
    @Value("\${KAKAO_CLIENT_ID_DEV}")
    val clientId: String,

    @Value("\${KAKAO_CLIENT_SECRET_DEV}")
    val clientSecret: String,

    @Value("\${KAKAO_REDIRECT_URI_DEV}")
    val redirectUri: String,

    @Value("\${KAKAO_LOGIN_URI_DEV}")
    val loginUri: String
)
