package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.UpdateStoreRequest
import upbrella.be.store.dto.response.AllStoreIntroductionResponse
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.dto.response.StoreFindByIdResponse
import upbrella.be.store.dto.response.StoreIntroductionsResponseByClassification
import upbrella.be.store.entity.ClassificationType
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.ClassificationReader
import upbrella.be.store.repository.StoreDetailReader
import upbrella.be.umbrella.service.UmbrellaService

@Service
class StoreDetailService(
    private val storeDetailReader: StoreDetailReader,
    private val classificationReader: ClassificationReader,
    private val businessHourService: BusinessHourService,
    private val umbrellaService: UmbrellaService,
) {

    @Transactional
    fun updateStore(storeId: Long, request: UpdateStoreRequest) {
        val storeDetailById = storeDetailReader.findByStoreMetaId(storeId)

        val classification = classificationReader.findByIdAndType(request.classificationId, ClassificationType.CLASSIFICATION)
        val subClassification = classificationReader.findByIdAndType(request.subClassificationId, ClassificationType.SUB_CLASSIFICATION)

        val storeMetaForUpdate = StoreMeta.createStoreMetaForUpdate(request, classification, subClassification)
        val foundStoreMeta = storeDetailById.storeMeta

        foundStoreMeta!!.updateStoreMeta(storeMetaForUpdate)
        storeDetailById.updateStore(foundStoreMeta, request)

        businessHourService.updateBusinessHours(foundStoreMeta, request.businessHours)
    }


    /**
     * storeId(storeMetaId)를 통해 가게 상세정보 response를 반환하는 메소드
     */
    @Transactional(readOnly = true)
    fun findStoreDetailByStoreId(storeId: Long): StoreFindByIdResponse {

        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)
        val availableUmbrellaCount = umbrellaService.countAvailableUmbrellaAtStore(storeId)

        return StoreFindByIdResponse.fromStoreDetail(storeDetail, availableUmbrellaCount)
    }

    @Transactional(readOnly = true)
    fun findAllStores(): List<SingleStoreResponse> {
        return storeDetailReader.findAllStoresForAdmin()
    }

    @Transactional(readOnly = true)
    fun findAllStoreIntroductions(): AllStoreIntroductionResponse {
        val storeDetails = storeDetailReader.findAllStores()

        val collected = storeDetails.groupBy { it.storeMeta!!.subClassification!!.id }

        // 같은 ID끼리 리스트로 모은 것을 StoreIntroductionsResponseByClassification으로 변환
        val storeDetailsByClassification = collected.entries.stream()
            .sorted(compareBy { it.key })
            .map { StoreIntroductionsResponseByClassification.of(it.key!!, it.value) }
            .toList()

        return AllStoreIntroductionResponse.of(storeDetailsByClassification)
    }
}
