package upbrella.be.user.dto.token

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class OauthToken(
    val accessToken: String = "",
    val refreshToken: String = "",
    val tokenType: String = "",
    val expiresIn: Long = 0
)
