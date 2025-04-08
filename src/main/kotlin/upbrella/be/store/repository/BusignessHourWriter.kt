package upbrella.be.store.repository

import org.springframework.stereotype.Component
import upbrella.be.store.entity.BusinessHour

@Component
class BusignessHourWriter(
    private val businessHourRepository: BusinessHourRepository,
) {
    fun deleteAllByStoreMetaId(storeMetaId: Long) {
        businessHourRepository.deleteAllByStoreMetaId(storeMetaId)
    }

    fun saveAll(businessHours: List<BusinessHour>) {
        businessHourRepository.saveAll(businessHours)
    }
}