package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.UpdateStoreRequest
import upbrella.be.store.dto.response.AllStoreIntroductionResponse
import upbrella.be.store.dto.response.SingleStoreResponse
import upbrella.be.store.dto.response.StoreFindByIdResponse
import upbrella.be.store.dto.response.StoreIntroductionsResponseByClassification
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.StoreDetailReader
import upbrella.be.store.repository.StoreDetailWriter
import upbrella.be.umbrella.repository.UmbrellaReader
import upbrella.be.umbrella.service.UmbrellaService

@Service
class StoreDetailService(
    private val classificationService: ClassificationService,
    private val umbrellaReader: UmbrellaReader,
    private val umbrellaService: UmbrellaService,
    private val storeDetailReader: StoreDetailReader,
    private val storeDetailWriter: StoreDetailWriter,
    private val businessHourService: BusinessHourService
) {

    @Transactional
    fun updateStore(storeId: Long, request: UpdateStoreRequest) {
        val storeDetailById = storeDetailReader.findByStoreMetaId(storeId)

        val classification = classificationService.findClassificationById(request.classificationId)
        val subClassification = classificationService.findSubClassificationById(request.subClassificationId)

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
        val availableUmbrellaCount = umbrellaReader.countRentableUmbrellasByStore(storeId)

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

    @Transactional
    fun saveStoreDetail(storeDetail: StoreDetail) {
        storeDetailWriter.
        save(storeDetail)
    }
}
