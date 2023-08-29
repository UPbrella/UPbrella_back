package upbrella.be.store.repository;

import upbrella.be.store.entity.StoreMeta;

import java.util.List;

public interface StoreMetaRepositoryCustom {

    List<StoreMeta> findAllStoresByClassification(long classificationId);
}
