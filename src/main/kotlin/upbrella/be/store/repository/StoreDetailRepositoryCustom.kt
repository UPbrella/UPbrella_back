package upbrella.be.store.repository

import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.entity.StoreDetail
import java.util.Optional

interface StoreDetailRepositoryCustom {
    fun findAllStores(): List<StoreDetail>

    fun findByStoreMetaIdUsingFetchJoin(storeMetaId: Long): Optional<StoreDetail>

    fun findAllStoresForAdmin(): List<SingleStoreResponse>
}