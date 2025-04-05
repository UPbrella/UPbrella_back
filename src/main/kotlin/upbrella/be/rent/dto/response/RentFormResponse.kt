package upbrella.be.rent.dto.response

import upbrella.be.umbrella.entity.Umbrella

data class RentFormResponse(
    val classificationName: String,
    val storeMetaId: Long,
    val rentStoreName: String,
    val umbrellaUuid: Long
) {
    companion object {
        fun of(umbrella: Umbrella): RentFormResponse {
            return RentFormResponse(
                classificationName = umbrella.storeMeta.classification!!.name!!,
                storeMetaId = umbrella.storeMeta.id!!,
                rentStoreName = umbrella.storeMeta.name,
                umbrellaUuid = umbrella.uuid
            )
        }
    }
}
