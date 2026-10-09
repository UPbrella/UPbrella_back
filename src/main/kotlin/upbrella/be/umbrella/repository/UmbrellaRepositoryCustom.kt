package upbrella.be.umbrella.repository

import org.springframework.data.domain.Pageable
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory
import upbrella.be.umbrella.entity.UmbrellaStatus

interface UmbrellaRepositoryCustom {

    fun countUmbrellasByStatus(): Map<UmbrellaStatus, Long>

    fun countUmbrellasByStatusAndStore(storeId: Long): Map<UmbrellaStatus, Long>

    fun findUmbrellaAndHistoryOrderedByUmbrellaId(pageable: Pageable): List<UmbrellaWithHistory>

    fun findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(storeId: Long, pageable: Pageable): List<UmbrellaWithHistory>
}
