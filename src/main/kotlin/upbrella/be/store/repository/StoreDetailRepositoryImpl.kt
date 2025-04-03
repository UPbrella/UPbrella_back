package upbrella.be.store.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import upbrella.be.store.dto.response.QSingleClassificationResponse
import upbrella.be.store.dto.response.QSingleStoreResponse
import upbrella.be.store.dto.response.QSingleSubClassificationResponse
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.QClassification.classification
import upbrella.be.store.entity.QStoreDetail.storeDetail
import upbrella.be.store.entity.QStoreImage.storeImage
import upbrella.be.store.entity.QStoreMeta.storeMeta
import java.util.Optional

class StoreDetailRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : StoreDetailRepositoryCustom {

    override fun findAllStores(): List<StoreDetail> {
        return queryFactory
            .selectFrom(storeDetail)
            .join(storeDetail.storeMeta, storeMeta).fetchJoin()
            .join(storeMeta.classification, classification).fetchJoin()
            .join(storeMeta.subClassification, classification).fetchJoin()
            .where(storeMeta.deleted.isFalse())
            .distinct()
            .fetch()
    }

    override fun findByStoreMetaIdUsingFetchJoin(storeMetaId: Long): Optional<StoreDetail> {
        return Optional.ofNullable(queryFactory
            .selectFrom(storeDetail)
            .join(storeDetail.storeMeta, storeMeta).fetchJoin()
            .leftJoin(storeDetail.storeImages, storeImage).fetchJoin()
            .where(storeDetail.storeMeta.id.eq(storeMetaId))
            .where(storeMeta.deleted.isFalse())
            .fetchOne())
    }

    override fun findAllStoresForAdmin(): List<SingleStoreResponse> {
        return queryFactory
            .select(
                QSingleStoreResponse(
                    storeDetail.storeMeta.id,
                    storeDetail.storeMeta.name,
                    storeDetail.storeMeta.category,
                    QSingleClassificationResponse(
                        storeDetail.storeMeta.classification.id,
                        storeDetail.storeMeta.classification.type,
                        storeDetail.storeMeta.classification.name,
                        storeDetail.storeMeta.classification.latitude,
                        storeDetail.storeMeta.classification.longitude
                    ),
                    QSingleSubClassificationResponse(
                        storeDetail.storeMeta.subClassification.id,
                        storeDetail.storeMeta.subClassification.type,
                        storeDetail.storeMeta.subClassification.name
                    ),
                    storeDetail.storeMeta.activated,
                    storeDetail.address,
                    storeDetail.addressDetail,
                    storeDetail.umbrellaLocation,
                    storeDetail.workingHour,
                    storeDetail.contactInfo,
                    storeDetail.instaUrl,
                    storeDetail.storeMeta.latitude,
                    storeDetail.storeMeta.longitude,
                    storeDetail.content
                )
            )
            .from(storeDetail)
            .join(storeDetail.storeMeta, storeMeta)
            .join(storeMeta.classification, classification)
            .join(storeMeta.subClassification, classification)
            .where(storeMeta.deleted.isFalse())
            .fetch()
    }
}