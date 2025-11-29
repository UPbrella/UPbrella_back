package upbrella.be.user.dto.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import upbrella.be.util.AppleJwtGenerator
import javax.annotation.PostConstruct

@Component
open class AppleOauthInfo(
    @Value("\${APPLE_CLIENT_ID_DEV}")
    val clientId: String,

    @Value("\${APPLE_TEAM_ID_DEV}")
    private val teamId: String,

    @Value("\${APPLE_KEY_ID_DEV}")
    private val keyId: String,

    @Value("\${APPLE_P8_KEY_PATH_DEV}")
    private val p8KeyPath: String,

    @Value("\${APPLE_REDIRECT_URI_DEV}")
    val redirectUri: String,

    @Value("\${APPLE_LOGIN_URI_DEV}")
    val loginUri: String,

    private val appleJwtGenerator: AppleJwtGenerator
) {
    private var _clientSecret: String = ""

    val clientSecret: String
        get() = _clientSecret

    @PostConstruct
    fun init() {
        // Apple JWT Client Secret을 동적으로 생성
        _clientSecret = appleJwtGenerator.generateClientSecret(
            teamId = teamId,
            keyId = keyId,
            clientId = clientId,
            p8KeyPath = p8KeyPath
        )
    }
}
