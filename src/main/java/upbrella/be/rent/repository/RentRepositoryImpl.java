package upbrella.be.rent.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import upbrella.be.rent.dto.response.HistoryInfoDto;
import upbrella.be.rent.dto.QTestDto;
import upbrella.be.rent.dto.request.HistoryFilterRequest;
import upbrella.be.rent.entity.History;

import java.util.List;

import static upbrella.be.rent.entity.QHistory.history;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;
import static upbrella.be.umbrella.entity.QUmbrella.umbrella;
import static upbrella.be.user.entity.QUser.user;

@RequiredArgsConstructor
public class RentRepositoryImpl implements RentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<History> findAll(HistoryFilterRequest filter, Pageable pageable) {

        return queryFactory
                .selectFrom(history)
                .join(history.user, user).fetchJoin()
                .leftJoin(history.refundedBy, user).fetchJoin()
                .join(history.umbrella, umbrella).fetchJoin()
                .join(history.rentStoreMeta, storeMeta).fetchJoin()
                .leftJoin(history.returnStoreMeta, storeMeta).fetchJoin()
                .where(filterRefunded(filter))
                .orderBy(history.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public long countAll(HistoryFilterRequest filter, Pageable pageable) {

        return queryFactory
                .selectFrom(history)
                .where(filterRefunded(filter))
                .fetchCount();
    }

    @Override
    public List<History> findAllByUserId(long userId) {

        return queryFactory
                .selectFrom(history)
                .join(history.user, user).fetchJoin()
                .leftJoin(history.refundedBy, user).fetchJoin()
                .join(history.umbrella, umbrella).fetchJoin()
                .join(history.rentStoreMeta, storeMeta).fetchJoin()
                .leftJoin(history.returnStoreMeta, storeMeta).fetchJoin()
                .where(history.user.id.eq(userId))
                .orderBy(history.id.desc())
                .fetch();
    }

    private BooleanExpression filterRefunded(HistoryFilterRequest filter) {

        if (filter.getRefunded() == null) {
            return null;
        }

        if (filter.getRefunded() == true) {
            return history.refundedAt.isNotNull();
        }

        return history.refundedAt.isNull();
    }

    @Override
    public List<HistoryInfoDto> findHistoryInfos(HistoryFilterRequest filter, Pageable pageable) {

        return queryFactory
                .select(new QTestDto(
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
                ))
                .from(history)
                .join(history.user, user)
                .join(history.umbrella, umbrella)
                .join(history.rentStoreMeta, storeMeta)
                .leftJoin(history.returnStoreMeta, storeMeta)
                .where(filterRefunded(filter))
                .orderBy(history.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
