package upbrella.be.store.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.store.dto.request.CreateStoreRequest
import upbrella.be.store.dto.response.AllCurrentLocationStoreResponse
import upbrella.be.store.dto.response.CurrentUmbrellaStoreResponse
import upbrella.be.store.dto.response.SingleCurrentLocationStoreResponse
import upbrella.be.store.dto.response.StoreMetaWithUmbrellaCount
import upbrella.be.store.entity.*
import upbrella.be.store.exception.DeletedStoreDetailException
import upbrella.be.store.exception.EssentialImageException
import upbrella.be.store.repository.*
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaRepository
import java.time.LocalDateTime

@Service
class StoreMetaService(
    private val umbrellaRepository: UmbrellaRepository,
    private val storeMetaReader: StoreMetaReader,
    private val storeMetaWriter: StoreMetaWriter,
    private val storeDetailReader: StoreDetailReader,
    private val storeDetailWriter: StoreDetailWriter,
    private val businessHourWriter: BusinessHourWriter,
    private val classificationReader: ClassificationReader
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
        val storeMetaWithUmbrellaCounts = storeMetaReader.findAllStoresByClassification(classificationId)

        return AllCurrentLocationStoreResponse.ofCreate(
            storeMetaWithUmbrellaCounts.map {
                mapToSingleCurrentLocationStoreResponse(it, currentTime)
            }
        )
    }

    @Transactional
    fun createStore(store: CreateStoreRequest) {
        val classification = classificationReader.findByIdAndType(store.classificationId, ClassificationType.CLASSIFICATION)
        val subClassification = classificationReader.findByIdAndType(store.subClassificationId, ClassificationType.SUB_CLASSIFICATION)

        val businessHourRequests = store.businessHours

        val storeMeta = storeMetaWriter.save(
            StoreMeta.createStoreMetaForSave(store, classification, subClassification)
        )

        val businessHours = businessHourRequests.map { businessHourRequest ->
            BusinessHour.ofCreateBusinessHour(businessHourRequest, storeMeta)
        }

        businessHourWriter.saveAll(businessHours)

        storeDetailWriter.save(StoreDetail.createForSave(store, storeMeta))
    }

    @Transactional
    fun deleteStoreMeta(storeMetaId: Long) {
        storeMetaReader.findById(storeMetaId).delete()
    }

    @Transactional
    fun activateStoreStatus(storeId: Long) {
        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)

        val storeImages: List<StoreImage> = storeDetail.storeImages
        if (storeImages.isEmpty()) {
            throw EssentialImageException("[ERROR] 가게 이미지가 존재하지 않으면 영업지점을 활성화할 수 없습니다.")
        }

        storeDetail.storeMeta!!.activateStoreStatus()
    }

    @Transactional
    fun inactivateStoreStatus(storeId: Long) {
        val storeDetail = storeDetailReader.findByStoreMetaId(storeId)
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
}