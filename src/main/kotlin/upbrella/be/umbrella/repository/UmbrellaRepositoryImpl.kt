package upbrella.be.umbrella.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import upbrella.be.rent.entity.QHistory
import upbrella.be.umbrella.dto.response.QUmbrellaWithHistory
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.QUmbrella.umbrella
import upbrella.be.umbrella.entity.UmbrellaStatus

class UmbrellaRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UmbrellaRepositoryCustom {

    override fun countUmbrellasByStatus(): Map<UmbrellaStatus, Long> =
        countByStatus(umbrella.deleted.eq(false))

    override fun countUmbrellasByStatusAndStore(storeId: Long): Map<UmbrellaStatus, Long> =
        countByStatus(umbrella.deleted.eq(false).and(umbrella.storeMeta.id.eq(storeId)))

    private fun countByStatus(condition: BooleanExpression): Map<UmbrellaStatus, Long> {
        val count = umbrella.count()

        return queryFactory.select(umbrella.status, count)
            .from(umbrella)
            .where(condition)
            .groupBy(umbrella.status)
            .fetch()
            .associate { it.get(umbrella.status)!! to it.get(count)!! }
    }

    override fun findUmbrellaAndHistoryOrderedByUmbrellaId(pageable: Pageable): List<UmbrellaWithHistory> {
        val subHistory = QHistory("subHistory")

        val subQuery = JPAQuery<Long>()
        subQuery.select(CaseBuilder()
            .`when`(subHistory.returnedAt.isNull)
            .then(subHistory.id)
            .otherwise(null as Long?))
            .from(subHistory)
            .where(subHistory.umbrella.id.eq(umbrella.id)
                .and(subHistory.id.eq(
                    JPAExpressions.select(subHistory.id.max())
                        .from(subHistory)
                        .where(subHistory.umbrella.id.eq(umbrella.id)))))
            .orderBy(subHistory.id.desc())
            .limit(1)

        return queryFactory.select(QUmbrellaWithHistory(
            umbrella.id,
            umbrella.storeMeta,
            umbrella.uuid,
            umbrella.status,
            umbrella.deleted,
            umbrella.createdAt,
            umbrella.etc,
            subQuery))
            .from(umbrella)
            .where(umbrella.deleted.eq(false))
            .orderBy(umbrella.uuid.asc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    override fun findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(
        storeId: Long,
        pageable: Pageable
    ): List<UmbrellaWithHistory> {
        val subHistory = QHistory("subHistory")

        val subQuery = JPAQuery<Long>()
        subQuery.select(CaseBuilder()
            .`when`(subHistory.returnedAt.isNull)
            .then(subHistory.id)
            .otherwise(null as Long?))
            .from(subHistory)
            .where(subHistory.umbrella.id.eq(umbrella.id)
                .and(subHistory.id.eq(
                    JPAExpressions.select(subHistory.id.max())
                        .from(subHistory)
                        .where(subHistory.umbrella.id.eq(umbrella.id)))))
            .orderBy(subHistory.id.desc())
            .limit(1)

        return queryFactory.select(QUmbrellaWithHistory(
            umbrella.id,
            umbrella.storeMeta,
            umbrella.uuid,
            umbrella.status,
            umbrella.deleted,
            umbrella.createdAt,
            umbrella.etc,
            subQuery))
            .from(umbrella)
            .where(umbrella.deleted.eq(false)
                .and(umbrella.storeMeta.id.eq(storeId)))
            .orderBy(umbrella.uuid.asc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }
}