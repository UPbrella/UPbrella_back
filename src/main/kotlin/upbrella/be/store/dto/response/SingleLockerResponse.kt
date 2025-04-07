package upbrella.be.store.dto.response

import upbrella.be.rent.entity.Locker

data class SingleLockerResponse(
    val id: Long,
    val storeMetaId: Long,
    val secretKey: String
) {
    companion object {
        fun fromLocker(locker: Locker): SingleLockerResponse {
            return SingleLockerResponse(
                id = locker.id ?: throw IllegalArgumentException("Locker ID must not be null"),
                storeMetaId = locker.storeMeta.id ?: throw IllegalArgumentException("StoreMeta ID must not be null"),
                secretKey = locker.secretKey
            )
        }
    }
}
