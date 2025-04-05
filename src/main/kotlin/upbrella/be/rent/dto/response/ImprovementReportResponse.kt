package upbrella.be.rent.dto.response

import upbrella.be.rent.entity.ImprovementReport

data class ImprovementReportResponse(
    val id: Long = 0,
    val umbrellaUuid: Long = 0,
    val content: String? = "",
    val etc: String? = ""
) {
    companion object {
        fun fromImprovementReport(improvementReport: ImprovementReport): ImprovementReportResponse {
            return ImprovementReportResponse(
                id = improvementReport.history?.id ?: 0,
                umbrellaUuid = improvementReport.history?.umbrella?.uuid ?: 0,
                content = improvementReport.content,
                etc = improvementReport.etc
            )
        }
    }
}
