package upbrella.be.store.repository;

import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount;

import java.util.List;

public interface StoreMetaRepositoryCustom {

    List<StoreMetaWithUmbrellaCount> findAllStoresByClassification(long classificationId);
}
