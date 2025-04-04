package upbrella.be.rent.service

import org.springframework.stereotype.Service
import upbrella.be.rent.dto.response.ImprovementReportPageResponse
import upbrella.be.rent.dto.response.ImprovementReportResponse
import upbrella.be.rent.entity.History
import upbrella.be.rent.entity.ImprovementReport
import upbrella.be.rent.repository.ImprovementReportRepository

@Service
class ImprovementReportService(
    private val improvementReportRepository: ImprovementReportRepository
) {

    fun findAll(): ImprovementReportPageResponse =
        ImprovementReportPageResponse.of(findAllImprovementReport())

    fun addImprovementReportFromReturn(history: History, content: String) {
        val report = ImprovementReport.createFromReturn(history, content)
        improvementReportRepository.save(report)
    }

    private fun findAllImprovementReport(): List<ImprovementReportResponse> =
        improvementReportRepository.findAll()
            .map { ImprovementReportResponse.fromImprovementReport(it) }
}
