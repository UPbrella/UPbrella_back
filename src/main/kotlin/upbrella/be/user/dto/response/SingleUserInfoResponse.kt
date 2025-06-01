package upbrella.be.user.dto.response

import upbrella.be.user.entity.User
import java.time.LocalDateTime

data class SingleUserInfoResponse(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val bank: String?,
    val accountNumber: String?,
    val adminStatus: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromUser(user: User): SingleUserInfoResponse {
            return SingleUserInfoResponse(
                id = user.id!!,
                name = user.name,
                phoneNumber = user.phoneNumber,
                email = user.email,
                bank = user.bank,
                accountNumber = user.accountNumber,
                adminStatus = user.adminStatus,
                createdAt = user.createdAt!!
            )
        }
    }
}
