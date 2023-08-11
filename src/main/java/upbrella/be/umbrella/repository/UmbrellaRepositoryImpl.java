package upbrella.be.umbrella.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
