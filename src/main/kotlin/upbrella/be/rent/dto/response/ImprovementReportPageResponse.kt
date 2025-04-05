package upbrella.be.rent.dto.response

data class ImprovementReportPageResponse(
    val improvementReports: List<ImprovementReportResponse>
) {
    companion object {
        fun of(improvementReports: List<ImprovementReportResponse>): ImprovementReportPageResponse {
            return ImprovementReportPageResponse(improvementReports)
        }
    }
}
