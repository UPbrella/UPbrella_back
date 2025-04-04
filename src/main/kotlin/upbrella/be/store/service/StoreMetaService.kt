package upbrella.be.store.service

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.CreateStoreRequest
import upbrella.be.store.dto.request.SingleBusinessHourRequest
import upbrella.be.store.dto.response.AllCurrentLocationStoreResponse
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.BusinessHour
import upbrella.be.store.entity.Classification
import upbrella.be.store.entity.StoreDetail
import upbrella.be.store.entity.StoreImage
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.exception.DeletedStoreDetailException
import upbrella.be.store.exception.EssentialImageException
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.store.repository.StoreMetaRepository
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaRepository
import java.time.LocalDateTime

@Service
class StoreMetaService(
    private val umbrellaRepository: UmbrellaRepository,
    private val storeMetaRepository: StoreMetaRepository,
    @Lazy private val storeDetailService: StoreDetailService,
    private val classificationService: ClassificationService,
    private val businessHourService: BusinessHourService
) {

    @Transactional(readOnly = true)
    fun findCurrentStoreIdByUmbrella(umbrellaId: Long): CurrentUmbrellaStoreResponse {
        val foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(umbrellaId)
            .orElseThrow { NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산입니다.") }

        if (foundUmbrella.storeMeta.deleted) {
            throw DeletedStoreDetailException("[ERROR] 삭제된 가게입니다.")
        }
        return CurrentUmbrellaStoreResponse.fromUmbrella(foundUmbrella)
    }

    @Transactional(readOnly = true)
    fun findAllStoresByClassification(classificationId: Long, currentTime: LocalDateTime): AllCurrentLocationStoreResponse {
        val storeMetaWithUmbrellaCounts = storeMetaRepository.findAllStoresByClassification(classificationId)

        return AllCurrentLocationStoreResponse.ofCreate(
            storeMetaWithUmbrellaCounts.map {
                mapToSingleCurrentLocationStoreResponse(it, currentTime)
            }
        )
    }

    @Transactional
    fun createStore(store: CreateStoreRequest) {
        val storeMeta = saveStoreMeta(store)
        saveStoreDetail(store, storeMeta)
    }

    @Transactional
    fun deleteStoreMeta(storeMetaId: Long) {
        findStoreMetaById(storeMetaId).delete()
    }

    @Transactional(readOnly = true)
    fun findStoreMetaById(id: Long): StoreMeta {
        return storeMetaRepository.findById(id)
            .orElseThrow { NonExistingStoreMetaException("[ERROR] 존재하지 않는 협업 지점 고유번호입니다.") }
    }

    @Transactional(readOnly = true)
    fun existByStoreId(storeId: Long): Boolean {
        return storeMetaRepository.existsById(storeId)
    }

    @Transactional(readOnly = true)
    fun existByClassificationId(classificationId: Long): Boolean {
        val store = storeMetaRepository.findByClassificationIdAndDeletedIsFalse(classificationId)
        return store.isPresent
    }

    @Transactional
    fun activateStoreStatus(storeId: Long) {
        val storeDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId)

        val storeImages: List<StoreImage> = storeDetail.storeImages
        if (storeImages.isEmpty()) {
            throw EssentialImageException("[ERROR] 가게 이미지가 존재하지 않으면 영업지점을 활성화할 수 없습니다.")
        }

        storeDetail.storeMeta!!.activateStoreStatus()
    }

    @Transactional
    fun inactivateStoreStatus(storeId: Long) {
        val storeDetail = storeDetailService.findStoreDetailByStoreMetaId(storeId)
        storeDetail.storeMeta!!.inactivateStoreStatus()
    }

    private fun isOpenStore(storeMetaWithUmbrellaCount: StoreMetaWithUmbrellaCount, currentTime: LocalDateTime): Boolean {
        val businessHours = storeMetaWithUmbrellaCount.storeMeta.businessHours

        return businessHours.stream()
            .filter { businessHour -> businessHour.date == currentTime.dayOfWeek }
            .filter { storeMetaWithUmbrellaCount.storeMeta.activated }
            .anyMatch { businessHour ->
                currentTime.toLocalTime().isAfter(businessHour.openAt) &&
                        currentTime.toLocalTime().isBefore(businessHour.closeAt)
            }
    }

    private fun mapToSingleCurrentLocationStoreResponse(
        storeMetaWithUmbrellaCount: StoreMetaWithUmbrellaCount,
        currentTime: LocalDateTime
    ): SingleCurrentLocationStoreResponse {
        return SingleCurrentLocationStoreResponse.fromStoreMeta(
            isOpenStore(storeMetaWithUmbrellaCount, currentTime), storeMetaWithUmbrellaCount
        )
    }

    private fun saveStoreDetail(store: CreateStoreRequest, storeMeta: StoreMeta) {
        storeDetailService.saveStoreDetail(StoreDetail.createForSave(store, storeMeta))
    }

    private fun saveStoreMeta(store: CreateStoreRequest): StoreMeta {
        val classification = classificationService.findClassificationById(store.classificationId)
        val subClassification = classificationService.findSubClassificationById(store.subClassificationId)

        val businessHourRequests = store.businessHours

        val storeMeta = storeMetaRepository.save(
            StoreMeta.createStoreMetaForSave(store, classification, subClassification)
        )

        val businessHours = businessHourRequests.map { businessHourRequest ->
            BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta)
        }

        businessHourService.saveAllBusinessHour(businessHours)

        return storeMeta
    }
}