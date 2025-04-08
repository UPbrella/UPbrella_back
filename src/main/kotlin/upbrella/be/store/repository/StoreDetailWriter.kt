package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.StoreDetail

@Component
class StoreDetailWriter(
    private val storeDetailRepository: StoreDetailRepository
) {
    fun save(storeDetail: StoreDetail) {
        storeDetailRepository.save(storeDetail)
    }
}
