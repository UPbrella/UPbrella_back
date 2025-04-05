package upbrella.be.user.dto.response

data class UmbrellaBorrowedByUserResponse(
    val uuid: Long,
    val elapsedDay: Int
) {
    companion object {
        fun of(uuid: Long, elapsedDay: Int): UmbrellaBorrowedByUserResponse {
            return UmbrellaBorrowedByUserResponse(
                uuid = uuid,
                elapsedDay = elapsedDay
            )
        }
    }
}
