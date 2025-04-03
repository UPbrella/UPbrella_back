package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.StoreDetail
import java.util.*

interface StoreDetailRepository : JpaRepository<StoreDetail, Long>, StoreDetailRepositoryCustom {
    fun findStoreDetailByStoreMetaId(storeMetaId: Long): Optional<StoreDetail>
}