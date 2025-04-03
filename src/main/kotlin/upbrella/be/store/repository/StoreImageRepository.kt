package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.StoreImage

interface StoreImageRepository : JpaRepository<StoreImage, Long> {
    fun findByStoreDetailId(storeDetailId: Long): List<StoreImage>
}