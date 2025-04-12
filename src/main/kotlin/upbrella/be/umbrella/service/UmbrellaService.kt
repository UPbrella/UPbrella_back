package upbrella.be.umbrella.service

import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import upbrella.be.rent.service.RentService
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.exception.NonExistingStoreMetaException
import upbrella.be.store.repository.StoreMetaReader
import upbrella.be.umbrella.dto.request.UmbrellaCreateRequest
import upbrella.be.umbrella.dto.request.UmbrellaModifyRequest
import upbrella.be.umbrella.dto.response.UmbrellaResponse
import upbrella.be.umbrella.dto.response.UmbrellaStatisticsResponse
import upbrella.be.umbrella.entity.Umbrella
import upbrella.be.umbrella.exception.ExistingUmbrellaUuidException
import upbrella.be.umbrella.exception.NonExistingUmbrellaException
import upbrella.be.umbrella.repository.UmbrellaRepository
import javax.transaction.Transactional

@Service
class UmbrellaService(
    private val umbrellaRepository: UmbrellaRepository,
    private val storeMetaReader: StoreMetaReader,
    @Lazy private val rentService: RentService
) {

    fun findAllUmbrellas(pageable: Pageable): List<UmbrellaResponse> =
        umbrellaRepository.findUmbrellaAndHistoryOrderedByUmbrellaId(pageable)
            .map { UmbrellaResponse.fromUmbrella(it) }

    fun findUmbrellasByStoreId(storeId: Long, pageable: Pageable): List<UmbrellaResponse> =
        umbrellaRepository.findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId, pageable)
            .map { UmbrellaResponse.fromUmbrella(it) }

    @Transactional
    fun addUmbrella(umbrellaCreateRequest: UmbrellaCreateRequest) {
        val storeMeta: StoreMeta = storeMetaReader.findById(umbrellaCreateRequest.storeMetaId)
        if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaCreateRequest.uuid)) {
            throw ExistingUmbrellaUuidException("[ERROR] 이미 존재하는 우산 관리 번호입니다.")
        }
        umbrellaRepository.save(Umbrella.ofCreated(umbrellaCreateRequest, storeMeta))
    }

    @Transactional
    fun modifyUmbrella(id: Long, umbrellaModifyRequest: UmbrellaModifyRequest) {
        val storeMeta: StoreMeta = storeMetaReader.findById(umbrellaModifyRequest.storeMetaId)
        val foundUmbrella = umbrellaRepository.findByIdAndDeletedIsFalse(id)
            .orElseThrow { NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다.") }

        if (foundUmbrella.uuid != umbrellaModifyRequest.uuid) {
            if (umbrellaRepository.existsByUuidAndDeletedIsFalse(umbrellaModifyRequest.uuid)) {
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
        umbrellaRepository.findByIdAndDeletedIsFalse(id)
            .orElseThrow { NonExistingUmbrellaException("[ERROR] 존재하지 않는 우산 고유번호입니다.") }

    fun countAvailableUmbrellaAtStore(storeMetaId: Long): Long =
        umbrellaRepository.countRentableUmbrellasByStore(storeMetaId)

    fun getUmbrellaAllStatistics(): UmbrellaStatisticsResponse {
        val totalUmbrella = umbrellaRepository.countAllUmbrellas()
        val availableUmbrella = umbrellaRepository.countRentableUmbrellas()
        val rentedUmbrella = umbrellaRepository.countRentedUmbrellas()
        val missingUmbrella = umbrellaRepository.countMissingUmbrellas()
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
        if (!storeMetaReader.existsById(storeId)) {
            throw NonExistingStoreMetaException("[ERROR] 존재하지 않는 매장 고유번호입니다.")
        }
        val totalUmbrellaByStoreId = umbrellaRepository.countAllUmbrellasByStore(storeId)
        val availableUmbrellaByStoreId = umbrellaRepository.countRentableUmbrellasByStore(storeId)
        val rentedUmbrellaByStoreId = umbrellaRepository.countRentedUmbrellasByStore(storeId)
        val missingUmbrellaByStoreId = umbrellaRepository.countMissingUmbrellasByStore(storeId)
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
