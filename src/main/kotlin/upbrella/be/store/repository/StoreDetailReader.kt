package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.exception.NonExistingStoreDetailException
import java.util.*

@Component
class StoreDetailReader(
    private val storeDetailRepository: StoreDetailRepository
) {
    fun findStoreDetailByStoreMetaId(storeMetaId: Long): StoreDetail {
        return storeDetailRepository.findStoreDetailByStoreMetaId(storeMetaId)
            .orElseThrow { NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다.") }
    }

    fun findAllStores(): List<StoreDetail> {
        return storeDetailRepository.findAllStores()
    }

    fun findByStoreMetaIdUsingFetchJoin(storeMetaId: Long): Optional<StoreDetail> {
        return storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId)
    }

    fun findAllStoresForAdmin(): List<SingleStoreResponse> {
        return storeDetailRepository.findAllStoresForAdmin()
    }
}
