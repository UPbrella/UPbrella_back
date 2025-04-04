package upbrella.be.user.dto.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import upbrella.be.user.dto.request.KakaoAccount
import java.io.Serializable

@JsonNaming(SnakeCaseStrategy::class)
data class KakaoLoginResponse(
    val id: Long? = null,
    val kakaoAccount: KakaoAccount? = null
) : Serializable
