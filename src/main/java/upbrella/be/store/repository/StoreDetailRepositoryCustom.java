package upbrella.be.store.repository;

import upbrella.be.store.dto.response.SingleStoreResponse;
import upbrella.be.store.entity.StoreDetail;

import java.util.List;
import java.util.Optional;

public interface StoreDetailRepositoryCustom {

    List<StoreDetail> findAllStores();

    Optional<StoreDetail> findByStoreMetaIdUsingFetchJoin(long storeMetaId);
}
