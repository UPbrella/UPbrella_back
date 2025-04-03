package upbrella.be.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import upbrella.be.store.entity.BusinessHour

interface BusinessHourRepository : JpaRepository<BusinessHour, Long> {
    fun findByStoreMetaId(storeMetaId: Long): List<BusinessHour>

    fun deleteAllByStoreMetaId(storeMetaId: Long)
}