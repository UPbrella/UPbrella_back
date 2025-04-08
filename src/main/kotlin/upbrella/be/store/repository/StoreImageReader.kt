package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.StoreImage

@Component
class StoreImageReader(
    private val storeImageRepository: StoreImageRepository
) {
    fun findById(id: Long): StoreImage? {
        return storeImageRepository.findById(id).orElse(null);
    }
}