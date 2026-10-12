package upbrella.be.umbrella.dto.request

import upbrella.be.umbrella.entity.UmbrellaStatus

data class UmbrellaCreateRequest (

    val storeMetaId: Long,
    val uuid: Long,
    val status: UmbrellaStatus? = null,
    // 하위 호환용. status가 없을 때만 쓴다
    val rentable: Boolean? = null,
    val etc: String? = null
) {
    fun toStatus(): UmbrellaStatus =
        status ?: UmbrellaStatus.fromLegacy(rentable = rentable ?: true, missed = false)
}
