package upbrella.be.rent.dto.response

data class ImprovementReportResponse(
    val id: Long = 0,
    val umbrellaUuid: Long = 0,
    val content: String? = "",
    val etc: String? = ""
)
