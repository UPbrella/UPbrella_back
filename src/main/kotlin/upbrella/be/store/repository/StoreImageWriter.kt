package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.StoreImage

@Component
class StoreImageWriter(
    private val storeImageRepository: StoreImageRepository
) {
    fun save(storeImage: StoreImage) {
        storeImageRepository.save(storeImage)
    }

    fun deleteById(id: Long) {
        storeImageRepository.deleteById(id)
    }
}