package upbrella.be.store.repository

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.exception.NonExistingStoreDetailException

@Component
class StoreDetailReader(
    private val storeDetailRepository: StoreDetailRepository
) {
    fun findAllStores(): List<StoreDetail> {
        return storeDetailRepository.findAllStores()
    }

    @Transactional(readOnly = true)
    fun findByStoreMetaId(storeMetaId: Long): StoreDetail {
        return storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeMetaId)
            .orElseThrow { NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다.") }
    }

    fun findAllStoresForAdmin(): List<SingleStoreResponse> {
        return storeDetailRepository.findAllStoresForAdmin()
    }
}
