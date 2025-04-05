package upbrella.be.store.dto.request

import javax.validation.constraints.Size

data class UpdateLockerRequest(
    val storeId: Long,
    @field:Size(min = 32)
    val secretKey: String
)
