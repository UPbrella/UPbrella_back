package upbrella.be.umbrella.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import upbrella.be.umbrella.dto.response.QUmbrellaWithHistory;
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory;

import java.util.List;

import static upbrella.be.rent.entity.QHistory.history;
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

    // TODO : history와 연동
    @Override
    public List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaId(Pageable pageable) {

        QUmbrellaWithHistory umbrellaWithHistory = new QUmbrellaWithHistory(
                umbrella.id,
                umbrella.storeMeta,
                umbrella.uuid,
                umbrella.rentable,
                umbrella.deleted,
                umbrella.createdAt,
                umbrella.etc,
                umbrella.missed
        );

        return queryFactory.select(umbrellaWithHistory)
                .from(umbrella)
                .join(umbrella.storeMeta)
                .where(umbrella.deleted.eq(false))
                .orderBy(umbrella.uuid.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(long storeId, Pageable pageable) {

        QUmbrellaWithHistory umbrellaWithHistory = new QUmbrellaWithHistory(
                umbrella.id,
                umbrella.storeMeta,
                umbrella.uuid,
                umbrella.rentable,
                umbrella.deleted,
                umbrella.createdAt,
                umbrella.etc,
                umbrella.missed,
                history.id
        );

        return queryFactory.select(umbrellaWithHistory)
                .from(history)
                .rightJoin(history.umbrella, umbrella)
                .join(umbrella.storeMeta)
                .where(umbrella.deleted.eq(false)
                        .and(umbrella.storeMeta.id.eq(storeId))
                        .and(history.returnedAt.isNull()))
                .orderBy(umbrella.uuid.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
