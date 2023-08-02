package upbrella.be.store.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.entity.StoreDetail;

import java.util.List;
import java.util.Optional;

import static upbrella.be.store.entity.QBusinessHour.businessHour;
import static upbrella.be.store.entity.QClassification.classification;
import static upbrella.be.store.entity.QStoreDetail.storeDetail;
import static upbrella.be.store.entity.QStoreImage.storeImage;
import static upbrella.be.store.entity.QStoreMeta.storeMeta;

@RequiredArgsConstructor
public class StoreDetailRepositoryImpl implements StoreDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreDetail> findAllStores() {

        return queryFactory
                .selectFrom(storeDetail)
                .join(storeDetail.storeMeta, storeMeta).fetchJoin()
                .join(storeMeta.classification, classification).fetchJoin()
                .join(storeMeta.subClassification, classification).fetchJoin()
                .join(storeMeta.businessHours, businessHour).fetchJoin()
                .leftJoin(storeDetail.storeImages, storeImage).fetchJoin()
                .distinct()
                .fetch();
    }

    @Override
    public Optional<StoreDetail> findByStoreMetaIdUsingFetchJoin(long storeMetaId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(storeDetail)
                .join(storeDetail.storeMeta, storeMeta).fetchJoin()
                .where(storeDetail.storeMeta.id.eq(storeMetaId))
                .where(storeMeta.deleted.isFalse())
                .fetchOne());
    }
}
