package upbrella.be.rent.dto.request

data class HistoryFilterRequest(
    val refunded: Boolean? = null,
    val storeId: Long? = null,
    val paid: Boolean? = null
)