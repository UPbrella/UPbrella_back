package upbrella.be.store.repository

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import upbrella.be.store.dto.response.QStoreMetaWithUmbrellaCount
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.QStoreMeta
import upbrella.be.umbrella.entity.QUmbrella

class StoreMetaRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : StoreMetaRepositoryCustom {

    override fun findAllStoresByClassification(classificationId: Long): List<StoreMetaWithUmbrellaCount> {
        val storeMeta = QStoreMeta.storeMeta
        val umbrella = QUmbrella.umbrella

        return queryFactory
            .select(
                QStoreMetaWithUmbrellaCount(
                    storeMeta,
                    JPAExpressions.select(umbrella.count())
                        .from(umbrella)
                        .where(
                            umbrella.storeMeta.id.eq(storeMeta.id),
                            umbrella.rentable.isTrue(),
                            umbrella.missed.isFalse(),
                            umbrella.deleted.isFalse()
                        )
                )
            )
            .from(storeMeta)
            .where(storeMeta.classification.id.eq(classificationId))
            .fetch()
    }
}