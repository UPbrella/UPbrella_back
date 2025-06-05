package upbrella.be.rent.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.response.HistoryInfoDto
import upbrella.be.rent.dto.response.QHistoryInfoDto
import upbrella.be.rent.entity.History
import upbrella.be.rent.entity.QHistory.history
import upbrella.be.store.entity.QStoreMeta.storeMeta
import upbrella.be.umbrella.entity.QUmbrella.umbrella
import upbrella.be.user.entity.QUser.user

class RentRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : RentRepositoryCustom {
    override fun findAll(filter: HistoryFilterRequest, pageable: Pageable): List<History> {
        return queryFactory
            .selectFrom(history)
            .join(history.user, user).fetchJoin()
            .leftJoin(history.refundedBy, user).fetchJoin()
            .join(history.umbrella, umbrella).fetchJoin()
            .join(history.rentStoreMeta, storeMeta).fetchJoin()
            .leftJoin(history.returnStoreMeta, storeMeta).fetchJoin()
            .where(
                filterRefunded(filter),
                filterPaid(filter),
                filterStore(filter)
            )
            .orderBy(history.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    override fun findHistoryInfos(
        filter: HistoryFilterRequest,
        pageable: Pageable
    ): List<HistoryInfoDto> {
        return queryFactory
            .select(
                QHistoryInfoDto(
                    history.id,
                    history.user.name,
                    history.user.phoneNumber,
                    history.rentStoreMeta.name,
                    history.rentedAt,
                    history.umbrella.uuid,
                    history.returnStoreMeta.name,
                    history.returnedAt,
                    history.paidAt,
                    history.bank,
                    history.accountNumber,
                    history.etc,
                    history.refundedAt
                )
            )
            .from(history)
            .join(history.user, user)
            .join(history.umbrella, umbrella)
            .join(history.rentStoreMeta, storeMeta)
            .leftJoin(history.returnStoreMeta, storeMeta)
            .where(
                filterRefunded(filter),
                filterPaid(filter),
                filterStore(filter)
            )
            .orderBy(history.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
    }

    override fun countAll(filter: HistoryFilterRequest, pageable: Pageable): Long {
        return queryFactory
            .selectFrom(history)
            .where(
                filterRefunded(filter),
                filterPaid(filter),
                filterStore(filter)
            )
            .fetch()
            .size.toLong()
    }

    override fun findAllByUserId(userId: Long): List<History> {
        return queryFactory
            .selectFrom(history)
            .join(history.user, user).fetchJoin()
            .leftJoin(history.refundedBy, user).fetchJoin()
            .join(history.umbrella, umbrella).fetchJoin()
            .join(history.rentStoreMeta, storeMeta).fetchJoin()
            .leftJoin(history.returnStoreMeta, storeMeta).fetchJoin()
            .where(history.user.id.eq(userId))
            .orderBy(history.id.desc())
            .fetch()
    }

    private fun filterRefunded(filter: HistoryFilterRequest): BooleanExpression? {
        if (filter.refunded == null) {
            return null
        }

        if (filter.refunded == true) {
            return history.refundedAt.isNotNull
        }

        return history.refundedAt.isNull
    }

    private fun filterPaid(filter: HistoryFilterRequest): BooleanExpression? {
        if (filter.paid == null) {
            return null
        }

        return if (filter.paid == true) {
            history.paidAt.isNotNull
        } else {
            history.paidAt.isNull
        }
    }

    private fun filterStore(filter: HistoryFilterRequest): BooleanExpression? {
        return filter.storeId?.let { history.rentStoreMeta.id.eq(it) }
    }
}
