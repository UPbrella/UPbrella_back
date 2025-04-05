package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.response.AllBusinessHourResponse
import upbrella.be.store.dto.response.SingleBusinessHourResponse
import upbrella.be.store.entity.BusinessHour
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.BusinessHourRepository

@Service
class BusinessHourService(
    private val businessHourRepository: BusinessHourRepository
) {

    @Transactional
    fun saveAllBusinessHour(businessHours: List<BusinessHour>) {
        businessHourRepository.saveAll(businessHours)
    }

    @Transactional(readOnly = true)
    fun findBusinessHourByStoreMetaId(storeMetaId: Long): List<BusinessHour> {
        return businessHourRepository.findByStoreMetaId(storeMetaId)
    }

    fun createBusinessHourResponse(businessHours: List<BusinessHour>): List<SingleBusinessHourResponse> {
        return businessHours.map {
            SingleBusinessHourResponse.createSingleHourResponse(it)
        }.sortedBy { it.date }
    }

    @Transactional(readOnly = true)
    fun findAllBusinessHours(storeId: Long): AllBusinessHourResponse {
        val businessHours = findBusinessHourByStoreMetaId(storeId)
        return AllBusinessHourResponse.builder()
            .businessHours(createBusinessHourResponse(businessHours))
            .build()
    }

    @Transactional
    fun updateBusinessHours(storeMeta: StoreMeta, businessHoursRequest: List<SingleBusinessHourRequest>) {
        businessHourRepository.deleteAllByStoreMetaId(storeMeta.id!!)

        val businessHours = businessHoursRequest.map { businessHourRequest ->
            BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta)
        }

        businessHourRepository.saveAll(businessHours)
    }
}