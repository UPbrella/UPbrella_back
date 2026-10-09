package upbrella.be.umbrella.dto.response

import upbrella.be.umbrella.entity.UmbrellaStatus

data class UmbrellaResponse (

    val id: Long,
    val historyId: Long? = null,
    val storeMetaId: Long? = null,
    val storeName: String,
    val uuid: Long,
    val status: UmbrellaStatus,
    // 하위 호환용. status가 사용 가능이면 true
    val rentable: Boolean,
    val etc: String? = null
)
{
    companion object {
        fun fromUmbrella(umbrellaWithHistory: UmbrellaWithHistory): UmbrellaResponse {

            return UmbrellaResponse(
                id = umbrellaWithHistory.id,
                historyId = umbrellaWithHistory.historyId,
                status = umbrellaWithHistory.status,
                rentable = umbrellaWithHistory.status == UmbrellaStatus.AVAILABLE,
                storeMetaId = umbrellaWithHistory.storeMeta.id,
                storeName = umbrellaWithHistory.storeMeta.name,
                uuid = umbrellaWithHistory.uuid,
                etc = umbrellaWithHistory.etc
            )
        }
    }
}
