package upbrella.be.store.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import upbrella.be.store.dto.response.QStoreMetaWithUmbrellaCount;
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount;
import upbrella.be.store.entity.QStoreMeta;
import upbrella.be.umbrella.entity.QUmbrella;

import java.util.List;


@RequiredArgsConstructor
public class StoreMetaRepositoryImpl implements StoreMetaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreMetaWithUmbrellaCount> findAllStoresByClassification(long classificationId) {

        QStoreMeta storeMeta = QStoreMeta.storeMeta;
        QUmbrella umbrella = QUmbrella.umbrella;

        return queryFactory
                .select(new QStoreMetaWithUmbrellaCount(
                        storeMeta,
                        JPAExpressions.select(umbrella.count())
                                .from(umbrella)
                                .where(umbrella.storeMeta.id.eq(storeMeta.id),
                                        umbrella.rentable.isTrue(),
                                        umbrella.missed.isFalse(),
                                        umbrella.deleted.isFalse())))
                .from(storeMeta)
                .where(storeMeta.classification.id.eq(classificationId))
                .fetch();
    }

}
