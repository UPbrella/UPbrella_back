package upbrella.be.umbrella.entity

enum class UmbrellaStatus {
    AVAILABLE, // 사용 가능
    RENTED, // 대여중
    UNLOCATED, // 위치 미확인
    LOST; // 분실

    fun isMissing(): Boolean = this == UNLOCATED || this == LOST

    companion object {
        // status 없이 rentable/missed만 보내는 기존 요청을 상태로 바꾼다
        fun fromLegacy(rentable: Boolean, missed: Boolean): UmbrellaStatus = when {
            missed -> LOST
            rentable -> AVAILABLE
            else -> RENTED
        }
    }
}
