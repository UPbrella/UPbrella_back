package upbrella.be.rent.dto.response

data class ConditionReportPageResponse(
    val conditionReports: List<ConditionReportResponse>
) {
    companion object {
        fun of(conditionReports: List<ConditionReportResponse>): ConditionReportPageResponse {
            return ConditionReportPageResponse(conditionReports)
        }
    }
}
