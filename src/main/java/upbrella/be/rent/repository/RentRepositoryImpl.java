package upbrella.be.rent.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
    public List<History> findAllByUserId(long userId) {

        return queryFactory
                .selectFrom(history)
                .join(history.user, user).fetchJoin()
                .leftJoin(history.refundedBy, user).fetchJoin()
                .join(history.umbrella, umbrella).fetchJoin()
                .join(history.rentStoreMeta, storeMeta).fetchJoin()
                .leftJoin(history.returnStoreMeta, storeMeta).fetchJoin()
                .where(history.user.id.eq(userId))
                .fetch();
    }
}
