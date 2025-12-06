package upbrella.be.user.dto.token

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import upbrella.be.util.AppleJwtGenerator
import javax.annotation.PostConstruct

interface AppleOauthInfo {
    val clientId: String
    val redirectUri: String
    val loginUri: String
    val clientSecret: String
}

@Component
@Profile("dev", "test")
class DevAppleOauthInfo(
    @Value("\${APPLE_CLIENT_ID_DEV}")
    override val clientId: String,

    @Value("\${APPLE_TEAM_ID_DEV}")
    private val teamId: String,

    @Value("\${APPLE_KEY_ID_DEV}")
    private val keyId: String,

    @Value("\${APPLE_P8_KEY_PATH_DEV}")
    private val p8KeyPath: String,

    @Value("\${APPLE_REDIRECT_URI_DEV}")
    override val redirectUri: String,

    @Value("\${APPLE_LOGIN_URI_DEV}")
    override val loginUri: String,

    private val appleJwtGenerator: AppleJwtGenerator
) : AppleOauthInfo {
    private var _clientSecret: String = ""

    override val clientSecret: String
        get() = _clientSecret

    @PostConstruct
    fun init() {
        _clientSecret = appleJwtGenerator.generateClientSecret(
            teamId = teamId,
            keyId = keyId,
            clientId = clientId,
            p8KeyPath = p8KeyPath
        )
    }
}

@Component
@Profile("prod")
class ProdAppleOauthInfo(
    @Value("\${APPLE_CLIENT_ID_PROD}")
    override val clientId: String,

    @Value("\${APPLE_TEAM_ID_PROD}")
    private val teamId: String,

    @Value("\${APPLE_KEY_ID_PROD}")
    private val keyId: String,

    @Value("\${APPLE_P8_KEY_PATH_PROD}")
    private val p8KeyPath: String,

    @Value("\${APPLE_REDIRECT_URI_PROD}")
    override val redirectUri: String,

    @Value("\${APPLE_LOGIN_URI_PROD}")
    override val loginUri: String,

    private val appleJwtGenerator: AppleJwtGenerator
) : AppleOauthInfo {
    private var _clientSecret: String = ""

    override val clientSecret: String
        get() = _clientSecret

    @PostConstruct
    fun init() {
        _clientSecret = appleJwtGenerator.generateClientSecret(
            teamId = teamId,
            keyId = keyId,
            clientId = clientId,
            p8KeyPath = p8KeyPath
        )
    }
}
