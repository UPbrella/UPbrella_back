package upbrella.be.umbrella.repository

import com.querydsl.core.types.dsl.CaseBuilder
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import upbrella.be.rent.entity.QHistory
import upbrella.be.umbrella.dto.response.QUmbrellaWithHistory
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.QUmbrella.umbrella

class UmbrellaRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UmbrellaRepositoryCustom {

    override fun countAllUmbrellas(): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.deleted.eq(false))
            .fetch()
            .size.toLong()
    }

    override fun countRentableUmbrellas(): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.rentable.eq(true)
                .and(umbrella.missed.eq(false))
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countRentedUmbrellas(): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.rentable.eq(false)
                .and(umbrella.missed.eq(false))
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countMissingUmbrellas(): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.missed.eq(true)
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countRentableUmbrellasByStore(storeMetaId: Long): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.storeMeta.id.eq(storeMetaId)
                .and(umbrella.rentable.eq(true))
                .and(umbrella.missed.eq(false))
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countRentedUmbrellasByStore(storeMetaId: Long): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.storeMeta.id.eq(storeMetaId)
                .and(umbrella.rentable.eq(false))
                .and(umbrella.missed.eq(false))
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countAllUmbrellasByStore(storeId: Long): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.storeMeta.id.eq(storeId)
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
    }

    override fun countMissingUmbrellasByStore(storeId: Long): Long {
        return queryFactory.selectFrom(umbrella)
            .where(umbrella.storeMeta.id.eq(storeId)
                .and(umbrella.missed.eq(true))
                .and(umbrella.deleted.eq(false)))
            .fetch()
            .size.toLong()
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
            umbrella.rentable,
            umbrella.deleted,
            umbrella.createdAt,
            umbrella.etc,
            umbrella.missed,
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
            umbrella.rentable,
            umbrella.deleted,
            umbrella.createdAt,
            umbrella.etc,
            umbrella.missed,
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