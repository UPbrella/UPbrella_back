package upbrella.be.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;

import static upbrella.be.store.entity.QBusinessHour.businessHour;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;

@RequiredArgsConstructor
public class StoreMetaRepositoryImpl implements StoreMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreMeta> findAllStoresByClassification(long classificationId) {

        return queryFactory
                .selectFrom(storeMeta)
                .leftJoin(storeMeta.businessHours, businessHour).fetchJoin()
                .where(storeMeta.deleted.eq(false)
                        .and(storeMeta.classification.id.eq(classificationId)))
                .distinct()
                .fetch();

    }
}
