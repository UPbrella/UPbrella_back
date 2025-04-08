package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.BusinessHour

@Component
class BusignessHourReader(
    private val businessHourRepository: BusinessHourRepository,
) {
    fun findByStoreMetaId(storeMetaId: Long): List<BusinessHour> {
        return businessHourRepository.findByStoreMetaId(storeMetaId)
    }
}