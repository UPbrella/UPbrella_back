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
    // 상태 관련 값을 하나도 보내지 않으면 지금 상태를 유지한다
    fun toStatus(current: UmbrellaStatus): UmbrellaStatus {
        if (status != null) {
            return status
        }
        if (rentable == null && missed == null) {
            return current
        }
        val legacyStatus = UmbrellaStatus.fromLegacy(rentable = rentable ?: false, missed = missed ?: false)
        // 기존 FE는 수정할 때 missed를 항상 false로, rentable은 응답값(위치 미확인·분실이면 false)으로 채워 보낸다.
        // 그래서 위치 미확인·분실 우산은 대여 가능으로 바꿀 때만 상태를 바꾼다
        if (current.isMissing() && legacyStatus != UmbrellaStatus.AVAILABLE) {
            return current
        }
        return legacyStatus
    }
}
