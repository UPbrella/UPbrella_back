package upbrella.be.rent.service

import org.springframework.stereotype.Service
import upbrella.be.rent.dto.response.ImprovementReportPageResponse
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.rent.repository.CustomImprovementReportRepository
import upbrella.be.rent.repository.ImprovementReportRepository

@Service
class ImprovementReportService(
    private val improvementReportRepository: ImprovementReportRepository,
    private val customImprovementReportRepository: CustomImprovementReportRepository
) {

    fun findAll(): ImprovementReportPageResponse =
        ImprovementReportPageResponse.of(customImprovementReportRepository.findAllImprovementReport())

    fun save(improvementReport: ImprovementReport) {

        improvementReportRepository.save(improvementReport)
    }
}
