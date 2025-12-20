package upbrella.be.user.dto.response

data class SocialUserSessionResponse(
    val name: String?,
    val email: String?,
    val provider: String? // "KAKAO" or "APPLE"
)
