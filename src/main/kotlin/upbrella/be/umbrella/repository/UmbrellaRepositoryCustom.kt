package upbrella.be.umbrella.repository

import org.springframework.data.domain.Pageable
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory

interface UmbrellaRepositoryCustom {

    fun countAllUmbrellas(): Long

    fun countRentableUmbrellas(): Long

    fun countRentedUmbrellas(): Long

    fun countMissingUmbrellas(): Long

    fun countRentableUmbrellasByStore(storeMetaId: Long): Long

    fun countRentedUmbrellasByStore(storeMetaId: Long): Long

    fun countAllUmbrellasByStore(storeId: Long): Long

    fun countMissingUmbrellasByStore(storeId: Long): Long

    fun findUmbrellaAndHistoryOrderedByUmbrellaId(pageable: Pageable): List<UmbrellaWithHistory>

    fun findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId: Long, pageable: Pageable): List<UmbrellaWithHistory>
}