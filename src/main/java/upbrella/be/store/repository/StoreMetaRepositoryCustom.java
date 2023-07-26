package upbrella.be.store.repository;

import upbrella.be.store.dto.response.SingleStoreResponse;

import java.util.List;

public interface StoreMetaRepositoryCustom {

    List<SingleStoreResponse> findAllStores();
}
