package upbrella.be.user.dto.response

import upbrella.be.user.entity.User
import java.io.Serializable

data class SessionUser(
    val id: Long,
    val socialId: Long? = null,
    val adminStatus: Boolean?
) : Serializable {
    companion object {
        fun fromUser(user: User): SessionUser {
            return SessionUser(
                id = user.id!!,
                socialId = user.socialId,
                adminStatus = user.adminStatus
            )
        }
    }
}
