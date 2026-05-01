package upbrella.be.rent.service

import org.springframework.stereotype.Service
import upbrella.be.rent.dto.response.ConditionReportPageResponse
import upbrella.be.rent.entity.ConditionReport
import upbrella.be.rent.repository.ConditionReportRepository
import upbrella.be.rent.repository.CustomConditionReportRepository

@Service
class ConditionReportService(
    private val conditionReportRepository: ConditionReportRepository,
    private val customConditionReportRepository: CustomConditionReportRepository,
) {

    fun findAll(): ConditionReportPageResponse =
        ConditionReportPageResponse.of(customConditionReportRepository.findAllConditionReport())

    fun saveConditionReport(conditionReport: ConditionReport) {
        conditionReportRepository.save(conditionReport)
    }
}
