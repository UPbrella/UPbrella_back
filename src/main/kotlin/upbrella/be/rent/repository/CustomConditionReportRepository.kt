package upbrella.be.rent.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import upbrella.be.rent.dto.response.ConditionReportResponse
import upbrella.be.rent.entity.QConditionReport.conditionReport
import upbrella.be.rent.entity.QHistory.history

@Repository
class CustomConditionReportRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllConditionReport(): List<ConditionReportResponse> {
        return queryFactory
            .select(
                Projections.constructor(
                    ConditionReportResponse::class.java,
                    conditionReport.id,
                    history.umbrella.uuid,
                    conditionReport.content,
                    conditionReport.etc
                )
            )
            .from(conditionReport)
            .join(history)
            .fetch()
    }
}