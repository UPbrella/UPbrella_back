package upbrella.be.umbrella.dto.response

import com.querydsl.core.annotations.QueryProjection
import upbrella.be.store.entity.StoreMeta
import upbrella.be.umbrella.entity.UmbrellaStatus
import java.time.LocalDateTime

data class UmbrellaWithHistory @QueryProjection constructor(
    val id: Long,
    val storeMeta: StoreMeta,
    val uuid: Long,
    val status: UmbrellaStatus,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val etc: String? = null,
    val historyId: Long? = null
)
