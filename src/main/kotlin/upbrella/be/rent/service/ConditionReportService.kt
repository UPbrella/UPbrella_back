package upbrella.be.rent.service

import org.springframework.stereotype.Service
import upbrella.be.rent.dto.response.ConditionReportPageResponse
import upbrella.be.rent.dto.response.ConditionReportResponse
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.repository.ConditionReportRepository

@Service
class ConditionReportService(
    private val conditionReportRepository: ConditionReportRepository
) {

    fun findAll(): ConditionReportPageResponse =
        ConditionReportPageResponse.of(findAllConditionReport())

    fun saveConditionReport(conditionReport: ConditionReport) {
        conditionReport.content
            ?.takeIf { it.isNotBlank() }
            ?.let { conditionReportRepository.save(conditionReport) }
    }

    private fun findAllConditionReport(): List<ConditionReportResponse> =
        conditionReportRepository.findAll()
            .map { ConditionReportResponse.fromConditionReport(it) }
}
