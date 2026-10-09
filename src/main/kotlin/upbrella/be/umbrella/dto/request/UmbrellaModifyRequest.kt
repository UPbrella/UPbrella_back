package upbrella.be.umbrella.dto.request

import upbrella.be.umbrella.entity.UmbrellaStatus

data class UmbrellaModifyRequest (

    val storeMetaId: Long,
    val uuid: Long,
    val status: UmbrellaStatus? = null,
    // 하위 호환용. status가 없을 때만 쓴다
    val rentable: Boolean? = null,
    val missed: Boolean? = null,
    val etc: String? = null
) {
    // 상태 관련 값을 하나도 보내지 않으면 null (상태를 바꾸지 않음)
    fun toStatus(): UmbrellaStatus? {
        if (status != null) {
            return status
        }
        if (rentable == null && missed == null) {
            return null
        }
        return UmbrellaStatus.fromLegacy(rentable = rentable ?: false, missed = missed ?: false)
    }
}
