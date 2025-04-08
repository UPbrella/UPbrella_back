package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.response.AllBusinessHourResponse
import upbrella.be.store.dto.response.SingleBusinessHourResponse
import upbrella.be.store.entity.BusinessHour
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.BusinessHourReader
import upbrella.be.store.repository.BusinessHourWriter

@Service
class BusinessHourService(
    private val businessHourReader: BusinessHourReader,
    private val businessHourWriter: BusinessHourWriter
) {

    @Transactional
    fun saveAllBusinessHour(businessHours: List<BusinessHour>) {
        businessHourWriter.saveAll(businessHours)
    }

    @Transactional(readOnly = true)
    fun findBusinessHourByStoreMetaId(storeMetaId: Long): List<BusinessHour> {
        return businessHourReader.findByStoreMetaId(storeMetaId)
    }

    fun createBusinessHourResponse(businessHours: List<BusinessHour>): List<SingleBusinessHourResponse> {
        return businessHours.map {
            SingleBusinessHourResponse.createSingleHourResponse(it)
        }.sortedBy { it.date }
    }

    @Transactional(readOnly = true)
    fun findAllBusinessHours(storeId: Long): AllBusinessHourResponse {
        val businessHours = findBusinessHourByStoreMetaId(storeId)

        return AllBusinessHourResponse(
            businessHours = createBusinessHourResponse(businessHours)
        )
    }

    @Transactional
    fun updateBusinessHours(storeMeta: StoreMeta, businessHoursRequest: List<SingleBusinessHourRequest>) {
        businessHourWriter.deleteAllByStoreMetaId(storeMeta.id!!)

        val businessHours = businessHoursRequest.map { businessHourRequest ->
            BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta)
        }

        businessHourWriter.saveAll(businessHours)
    }
}