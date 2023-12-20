package upbrella.be.umbrella.repository;

import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import upbrella.be.rent.entity.QHistory;
import upbrella.be.umbrella.dto.response.QUmbrellaWithHistory;
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory;

import java.util.List;

import static upbrella.be.umbrella.entity.QUmbrella.umbrella;

@RequiredArgsConstructor
public class UmbrellaRepositoryImpl implements UmbrellaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countAllUmbrellas() {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.deleted.eq(false))
                .fetchCount();
    }

    @Override
    public long countRentableUmbrellas() {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.rentable.eq(true)
                        .and(umbrella.missed.eq(false))
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countRentedUmbrellas() {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.rentable.eq(false)
                        .and(umbrella.missed.eq(false))
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countMissingUmbrellas() {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.missed.eq(true)
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countRentableUmbrellasByStore(long storeMetaId) {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.storeMeta.id.eq(storeMetaId)
                        .and(umbrella.rentable.eq(true))
                        .and(umbrella.missed.eq(false))
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countRentedUmbrellasByStore(long storeMetaId) {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.storeMeta.id.eq(storeMetaId)
                        .and(umbrella.rentable.eq(false))
                        .and(umbrella.missed.eq(false))
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countAllUmbrellasByStore(long storeId) {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.storeMeta.id.eq(storeId)
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public long countMissingUmbrellasByStore(long storeId) {

        return queryFactory.selectFrom(umbrella)
                .where(umbrella.storeMeta.id.eq(storeId)
                        .and(umbrella.missed.eq(true))
                        .and(umbrella.deleted.eq(false)))
                .fetchCount();
    }

    @Override
    public List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaId(Pageable pageable) {

        QHistory subHistory = new QHistory("subHistory");

        JPAQuery<Long> subQuery = new JPAQuery<>();
        subQuery.select(new CaseBuilder()
                        .when(subHistory.returnedAt.isNull())
                        .then(subHistory.id)
                        .otherwise((Long) null))
                .from(subHistory)
                .where(subHistory.umbrella.id.eq(umbrella.id)
                        .and(subHistory.id.eq(
                                JPAExpressions.select(subHistory.id.max())
                                        .from(subHistory)
                                        .where(subHistory.umbrella.id.eq(umbrella.id)))))
                .orderBy(subHistory.id.desc())
                .limit(1);

        return queryFactory.select(new QUmbrellaWithHistory(
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(long storeId, Pageable pageable) {

        QHistory subHistory = new QHistory("subHistory");

        JPAQuery<Long> subQuery = new JPAQuery<>();
        subQuery.select(new CaseBuilder()
                        .when(subHistory.returnedAt.isNull())
                        .then(subHistory.id)
                        .otherwise((Long) null))
                .from(subHistory)
                .where(subHistory.umbrella.id.eq(umbrella.id)
                        .and(subHistory.id.eq(
                                JPAExpressions.select(subHistory.id.max())
                                        .from(subHistory)
                                        .where(subHistory.umbrella.id.eq(umbrella.id)))))
                .orderBy(subHistory.id.desc())
                .limit(1);

        return queryFactory.select(new QUmbrellaWithHistory(
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
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
