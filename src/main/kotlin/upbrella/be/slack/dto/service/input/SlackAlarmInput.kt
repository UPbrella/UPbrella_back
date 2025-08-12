package upbrella.be.slack.dto.service.input

data class NotifyRentInput(
    val userId: Long,
    val userName: String,
    val rentStoreName: String,
)

data class NotifyConditionReportInput(
    val umbrellaId: Long,
    val rentStoreName: String,
    val content: String,
)

data class NotifyReturnInput(
    val userId: Long,
    val rentStoreName: String,
    val rentedAt: String,
    val returnStoreName: String,
    val returnedAt: String?,
    val unrefundedCount: Long,
)

data class NotifyImprovementReportInput(
    val umbrellaId: Long,
    val rentStoreName: String,
    val rentedAt: String,
    val returnStoreName: String,
    val returnedAt: String?,
    val content: String,
)
