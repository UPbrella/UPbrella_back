package upbrella.be.rent.dto.response

import upbrella.be.rent.entity.History
import upbrella.be.store.entity.StoreMeta

data class ReturnFormResponse(
    val classificationName: String,
    val rentStoreName: String,
    val storeId: Long
) {
    companion object {
        fun of(storeMeta: StoreMeta, history: History): ReturnFormResponse {
            return ReturnFormResponse(
                classificationName = storeMeta.classification!!.name!!,
                rentStoreName = history.rentStoreMeta.name,
                storeId = storeMeta.id!!
            )
        }
    }
}
