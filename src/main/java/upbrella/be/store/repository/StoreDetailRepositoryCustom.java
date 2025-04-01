package upbrella.be.store.repository;

import upbrella.be.store.dto.response.SingleStoreResponse;

import java.util.List;
import java.util.Optional;
import upbrella.be.store.entity.StoreDetail;

public interface StoreDetailRepositoryCustom {

    List<StoreDetail> findAllStores();

    Optional<StoreDetail> findByStoreMetaIdUsingFetchJoin(long storeMetaId);

    List<SingleStoreResponse> findAllStoresForAdmin();
}
