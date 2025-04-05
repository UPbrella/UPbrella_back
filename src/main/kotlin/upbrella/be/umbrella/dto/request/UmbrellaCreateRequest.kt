package upbrella.be.umbrella.dto.request;

data class UmbrellaCreateRequest (

    val storeMetaId: Long,
    val uuid: Long,
    val rentable: Boolean,
    val etc: String? = null
)
