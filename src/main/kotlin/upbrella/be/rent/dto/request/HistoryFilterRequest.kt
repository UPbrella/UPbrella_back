package upbrella.be.rent.dto.request

data class HistoryFilterRequest(
    val refunded: Boolean? = null,
    // true면 반납 기한이 지났는데 반납하지 않은 대여 내역만 조회한다
    val overdue: Boolean? = null
)