package upbrella.be.user.dto.response

import upbrella.be.user.entity.User

data class UserInfoResponse(
    val id: Long,
    val name: String,
    val phoneNumber: String?,
    val bank: String?,
    val accountNumber: String?,
    val email: String,
    val adminStatus: Boolean
) {
    companion object {
        @JvmStatic
        fun fromUser(user: User): UserInfoResponse {
            return UserInfoResponse(
                id = user.id!!,
                name = user.name,
                phoneNumber = user.phoneNumber,
                bank = user.bank,
                accountNumber = user.accountNumber,
                email = user.email,
                adminStatus = user.adminStatus
            )
        }
    }
}
