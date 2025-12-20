package upbrella.be.user.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AppleLoginResponse(
    @JsonProperty("sub")
    val sub: String?, // Apple user identifier

    @JsonProperty("email")
    val email: String?,

    @JsonProperty("email_verified")
    val emailVerified: Boolean?,

    // 첫 로그인 시에만 제공되는 사용자 이름
    var name: String? = null
)
