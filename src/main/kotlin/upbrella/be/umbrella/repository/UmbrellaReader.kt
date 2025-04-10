package upbrella.be.umbrella.repository

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.Umbrella

@Component
class UmbrellaReader(
    private val umbrellaRepository: UmbrellaRepository,
) {
    fun findById(id: Long): Umbrella? {
        return umbrellaRepository.findByIdAndDeletedIsFalse(id).orElse(null)
    }

    fun existsByUuid(uuid: Long): Boolean {
        return umbrellaRepository.existsByUuidAndDeletedIsFalse(uuid)
    }

    fun findUmbrellaAndHistoryOrderedByUmbrellaId(pageable: Pageable): List<UmbrellaWithHistory> {
        return umbrellaRepository.findUmbrellaAndHistoryOrderedByUmbrellaId(pageable)
    }

    fun findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId: Long, pageable: Pageable): List<UmbrellaWithHistory> {
        return umbrellaRepository.findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId, pageable)
    }

    fun countAllUmbrellas(): Long {
        return umbrellaRepository.countAllUmbrellas()
    }

    fun countRentableUmbrellas(): Long {
        return umbrellaRepository.countRentableUmbrellas()
    }

    fun countRentedUmbrellas(): Long {
        return umbrellaRepository.countRentedUmbrellas()
    }

    fun countMissingUmbrellas(): Long {
        return umbrellaRepository.countMissingUmbrellas()
    }

    fun countRentableUmbrellasByStore(storeMetaId: Long): Long {
        return umbrellaRepository.countRentableUmbrellasByStore(storeMetaId)
    }

    fun countAllUmbrellasByStore(storeId: Long): Long {
        return umbrellaRepository.countAllUmbrellasByStore(storeId)
    }

    fun countMissingUmbrellasByStore(storeId: Long): Long {
        return umbrellaRepository.countMissingUmbrellasByStore(storeId)
    }

    fun countRentedUmbrellasByStore(storeMetaId: Long): Long {
        return umbrellaRepository.countRentedUmbrellasByStore(storeMetaId)
    }
}