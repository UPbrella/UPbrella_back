package upbrella.be.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.dto.response.QStoreMetaWithUmbrellaCount;
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount;

import java.util.List;

import static upbrella.be.store.entity.QBusinessHour.businessHour;
import static upbrella.be.store.entity.QClassification.classification;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;
import static upbrella.be.umbrella.entity.QUmbrella.umbrella;

@RequiredArgsConstructor
public class StoreMetaRepositoryImpl implements StoreMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreMetaWithUmbrellaCount> findAllStoresByClassification(long classificationId) {

        QStoreMetaWithUmbrellaCount storeMetaWithUmbrellaCount = new QStoreMetaWithUmbrellaCount(
                storeMeta,
                umbrella.id.countDistinct().as("rentableUmbrellasCount")
        );


        return queryFactory
                .select(storeMetaWithUmbrellaCount)
                .from(umbrella)
                .rightJoin(umbrella.storeMeta, storeMeta)
                .leftJoin(storeMeta.businessHours, businessHour).fetchJoin()
                .leftJoin(storeMeta.classification, classification).fetchJoin()
                .where(storeMeta.deleted.eq(false)
                        .and(storeMeta.classification.id.eq(classificationId))
                        .and(umbrella.rentable.eq(true))
                        .and(umbrella.missed.eq(false))
                        .and(umbrella.deleted.eq(false)))
                .distinct()
                .groupBy(storeMeta, businessHour.id)
                .fetch();
    }
}
