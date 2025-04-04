package upbrella.be.umbrella.dto.response

import com.querydsl.core.annotations.QueryProjection
import upbrella.be.store.entity.StoreMeta
import java.time.LocalDateTime

data class UmbrellaWithHistory @QueryProjection constructor(
    val id: Long,
    val storeMeta: StoreMeta,
    val uuid: Long,
    val rentable: Boolean,
    val deleted: Boolean,
    val createdAt: LocalDateTime,
    val etc: String,
    val missed: Boolean,
    val historyId: Long? = null
)
