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
import upbrella.be.store.exception.NonExistingStoreDetailException
import upbrella.be.store.repository.StoreDetailRepository
import upbrella.be.umbrella.service.UmbrellaService

@Service
class StoreDetailService(
    private val classificationService: ClassificationService,
    private val umbrellaService: UmbrellaService,
    private val storeDetailRepository: StoreDetailRepository,
    private val businessHourService: BusinessHourService
) {

    @Transactional
    fun updateStore(storeId: Long, request: UpdateStoreRequest) {
        val storeDetailById = findStoreDetailByStoreMetaId(storeId)

        val classification = classificationService.findClassificationById(request.classificationId)
        val subClassification = classificationService.findSubClassificationById(request.subClassificationId)

        val storeMetaForUpdate = StoreMeta.createStoreMetaForUpdate(request, classification, subClassification)
        val foundStoreMeta = storeDetailById.storeMeta

        foundStoreMeta!!.updateStoreMeta(storeMetaForUpdate)
        storeDetailById.updateStore(foundStoreMeta, request)

        businessHourService.updateBusinessHours(foundStoreMeta, request.businessHours)
    }

    @Transactional(readOnly = true)
    fun findStoreDetailByStoreMetaId(storeId: Long): StoreDetail {
        return storeDetailRepository.findByStoreMetaIdUsingFetchJoin(storeId)
            .orElseThrow { NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다.") }
    }

    @Transactional(readOnly = true)
    fun findStoreDetailByStoreId(storeId: Long): StoreFindByIdResponse {
        val storeDetail = findStoreDetailByStoreMetaId(storeId)
        val availableUmbrellaCount = umbrellaService.countAvailableUmbrellaAtStore(storeId)

        return StoreFindByIdResponse.fromStoreDetail(storeDetail, availableUmbrellaCount)
    }

    @Transactional(readOnly = true)
    fun findAllStores(): List<SingleStoreResponse> {
        return storeDetailRepository.findAllStoresForAdmin()
    }

    @Transactional(readOnly = true)
    fun findAllStoreIntroductions(): AllStoreIntroductionResponse {
        val storeDetails = storeDetailRepository.findAllStores()

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
        storeDetailRepository.save(storeDetail)
    }

    @Transactional(readOnly = true)
    fun findByStoreMetaId(storeId: Long): StoreDetail {
        return storeDetailRepository.findStoreDetailByStoreMetaId(storeId)
            .orElseThrow { NonExistingStoreDetailException("[ERROR] 존재하지 않는 가게입니다.") }
    }
}