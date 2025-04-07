package upbrella.be.umbrella.dto.response;

data class UmbrellaResponse (

    val id: Long,
    val historyId: Long? = null,
    val storeMetaId: Long? = null,
    val storeName: String,
    val uuid: Long,
    val rentable: Boolean,
    val etc: String? = null
)
{
    companion object {
        fun fromUmbrella(umbrellaWithHistory: UmbrellaWithHistory): UmbrellaResponse {

            return UmbrellaResponse(
                id = umbrellaWithHistory.id,
                historyId = umbrellaWithHistory.historyId,
                rentable = umbrellaWithHistory.rentable,
                storeMetaId = umbrellaWithHistory.storeMeta.id,
                storeName = umbrellaWithHistory.storeMeta.name,
                uuid = umbrellaWithHistory.uuid,
                etc = umbrellaWithHistory.etc
            )
        }
    }
}
