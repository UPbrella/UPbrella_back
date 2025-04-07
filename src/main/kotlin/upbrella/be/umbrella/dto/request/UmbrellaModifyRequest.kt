package upbrella.be.umbrella.dto.request;

data class UmbrellaModifyRequest (

    val storeMetaId: Long,
    val uuid: Long,
    val rentable: Boolean,
    val missed: Boolean,
    val etc: String? = null
)
