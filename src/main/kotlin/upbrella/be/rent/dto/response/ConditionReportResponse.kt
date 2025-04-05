package upbrella.be.rent.dto.response

import upbrella.be.rent.entity.ConditionReport

data class ConditionReportResponse(
    val id: Long,
    val umbrellaUuid: Long,
    val content: String?,
    val etc: String?
) {
    companion object {
        fun fromConditionReport(conditionReport: ConditionReport): ConditionReportResponse {
            return ConditionReportResponse(
                id = conditionReport.history!!.id!!,
                umbrellaUuid = conditionReport.history.umbrella.uuid,
                content = conditionReport.content,
                etc = conditionReport.etc
            )
        }
    }
}
