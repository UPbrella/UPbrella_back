package upbrella.be.umbrella.service

import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import upbrella.be.rent.service.RentService
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.store.service.StoreMetaService
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaReader
import upbrella.be.umbrella.repository.UmbrellaWriter
import javax.transaction.Transactional

@Service
class UmbrellaService(
    private val umbrellaReader: UmbrellaReader,
    private val umbrellaWriter: UmbrellaWriter,
    private val storeMetaService: StoreMetaService,
    @Lazy private val rentService: RentService
) {

    fun findAllUmbrellas(pageable: Pageable): List<UmbrellaResponse> =
        umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaId(pageable)
            .map { UmbrellaResponse.fromUmbrella(it) }

    fun findUmbrellasByStoreId(storeId: Long, pageable: Pageable): List<UmbrellaResponse> =
        umbrellaReader.findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId, pageable)
            .map { UmbrellaResponse.fromUmbrella(it) }

    @Transactional
    fun addUmbrella(umbrellaCreateRequest: UmbrellaCreateRequest) {
        val storeMeta: StoreMeta =
            storeMetaService.findStoreMetaById(umbrellaCreateRequest.storeMetaId)
        if (umbrellaReader.existsByUuid(umbrellaCreateRequest.uuid)) {
            throw ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리 번호입니다.")
        }
        umbrellaWriter.save(Umbrella.ofCreated(umbrellaCreateRequest, storeMeta))
    }

    @Transactional
    fun modifyUmbrella(id: Long, umbrellaModifyRequest: UmbrellaModifyRequest) {
        val storeMeta: StoreMeta =
            storeMetaService.findStoreMetaById(umbrellaModifyRequest.storeMetaId)

        val foundUmbrella = umbrellaReader.findById(id)
            ?: throw NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다.")

        if (foundUmbrella.uuid != umbrellaModifyRequest.uuid) {
            if (umbrellaReader.existsByUuid(umbrellaModifyRequest.uuid)) {
                throw ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리 번호입니다.")
            }
        }
        foundUmbrella.update(umbrellaModifyRequest, storeMeta)
    }

    @Transactional
    fun deleteUmbrella(id: Long) {
        val foundUmbrella = findUmbrellaById(id)
        foundUmbrella.delete()
    }

    fun findUmbrellaById(id: Long): Umbrella =
        umbrellaReader.findById(id)
            ?: throw NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다.")

    /**
     * TODO
     * Persistance Layer 테스트 작성 후 삭제 (Service Layer에서 사용하지 않음)
     * 기존 ServiceTest에서도 삭제
     */
    fun countAvailableUmbrellaAtStore(storeMetaId: Long): Long =
        umbrellaReader.countRentableUmbrellasByStore(storeMetaId)

    fun getUmbrellaAllStatistics(): UmbrellaStatisticsResponse {
        val totalUmbrella = umbrellaReader.countAllUmbrellas()
        val availableUmbrella = umbrellaReader.countRentableUmbrellas()
        val rentedUmbrella = umbrellaReader.countRentedUmbrellas()
        val missingUmbrella = umbrellaReader.countMissingUmbrellas()
        val totalRent = rentService.countTotalRent()

        return UmbrellaStatisticsResponse.fromCounts(
            totalUmbrella,
            availableUmbrella,
            rentedUmbrella,
            missingUmbrella,
            totalRent
        )
    }

    fun getUmbrellaStatisticsByStoreId(storeId: Long): UmbrellaStatisticsResponse {
        if (!storeMetaService.existByStoreId(storeId)) {
            throw NonExistingStoreMetaException("[ERROR] 존재하지 않는 매장 고유번호입니다.")
        }
        val totalUmbrellaByStoreId = umbrellaReader.countAllUmbrellasByStore(storeId)
        val availableUmbrellaByStoreId = umbrellaReader.countRentableUmbrellasByStore(storeId)
        val rentedUmbrellaByStoreId = umbrellaReader.countRentedUmbrellasByStore(storeId)
        val missingUmbrellaByStoreId = umbrellaReader.countMissingUmbrellasByStore(storeId)
        val totalRentByStoreId = rentService.countTotalRentByStoreId(storeId)

        return UmbrellaStatisticsResponse.fromCounts(
            totalUmbrellaByStoreId,
            availableUmbrellaByStoreId,
            rentedUmbrellaByStoreId,
            missingUmbrellaByStoreId,
            totalRentByStoreId
        )
    }
}
