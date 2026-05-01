package upbrella.be.rent.dto.response

data class ConditionReportResponse(
    val id: Long,
    val umbrellaUuid: Long,
    val content: String?,
    val etc: String?
)
