package upbrella.be.rent.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import upbrella.be.rent.dto.response.ImprovementReportResponse
import upbrella.be.rent.entity.QHistory.history
import upbrella.be.rent.entity.QImprovementReport.improvementReport

@Repository
class CustomImprovementReportRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findAllImprovementReport(): List<ImprovementReportResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    ImprovementReportResponse::class.java,
                    improvementReport.id,
                    history.umbrella.uuid,
                    improvementReport.content,
                    improvementReport.etc
                )
            )
            .from(improvementReport)
            .join(history)
            .fetchJoin()
            .fetch()
    }
}
