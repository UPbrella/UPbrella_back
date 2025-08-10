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
