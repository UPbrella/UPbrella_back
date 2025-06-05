package upbrella.be.rent.service

import org.springframework.stereotype.Service
import upbrella.be.rent.dto.response.ImprovementReportPageResponse
import upbrella.be.rent.dto.response.ImprovementReportResponse
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.rent.repository.ImprovementReportRepository

@Service
class ImprovementReportService(
    private val improvementReportRepository: ImprovementReportRepository
) {

    fun findAll(): ImprovementReportPageResponse =
        ImprovementReportPageResponse.of(findAllImprovementReport())

    fun save(improvementReport: ImprovementReport) {
        improvementReport.content
            ?.takeIf { it.isNotBlank() }
            ?.let { improvementReportRepository.save(improvementReport) }
    }

    private fun findAllImprovementReport(): List<ImprovementReportResponse> =
        improvementReportRepository.findAll()
            .map { ImprovementReportResponse.fromImprovementReport(it) }
}
