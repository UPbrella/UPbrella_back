package upbrella.be.store.dto.response

import com.querydsl.core.annotations.QueryProjection
import upbrella.be.store.entity.StoreMeta

data class StoreMetaWithUmbrellaCount @QueryProjection constructor(
    val storeMeta: StoreMeta,
    val rentableUmbrellasCount: Long
)
