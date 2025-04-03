package upbrella.be.rent.repository

import org.springframework.data.domain.Pageable
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.response.HistoryInfoDto
import upbrella.be.rent.entity.History

interface RentRepositoryCustom {

    fun findAll(filter: HistoryFilterRequest, pageable: Pageable): List<History>

    fun findHistoryInfos(filter: HistoryFilterRequest, pageable: Pageable): List<HistoryInfoDto>

    fun countAll(filter: HistoryFilterRequest, pageable: Pageable): Long

    fun findAllByUserId(userId: Long): List<History>
}