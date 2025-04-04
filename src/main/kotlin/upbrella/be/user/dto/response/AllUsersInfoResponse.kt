package upbrella.be.user.dto.response

import upbrella.be.user.entity.User

data class AllUsersInfoResponse(val users: List<SingleUserInfoResponse>) {
    companion object {
        fun fromUsers(users: List<User?>): AllUsersInfoResponse {
            return AllUsersInfoResponse(
                users = users.map { SingleUserInfoResponse.fromUser(it!!) }
            )
        }
    }
}