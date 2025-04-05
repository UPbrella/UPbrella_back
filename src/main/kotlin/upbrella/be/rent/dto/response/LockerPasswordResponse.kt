package upbrella.be.rent.dto.response

class LockerPasswordResponse(password: String) {
    val password: String
    init {
        this.password = password.substring(0, 4)
    }
}
