package upbrella.be.rent.dto.request

import javax.validation.constraints.Size

data class RentUmbrellaByUserRequest(
    val region: String? = null,
    val storeId: Long = 0,
    val umbrellaId: Long = 0,

    @field:Size(max = 400, message = "conditionReport는 최대 400자여야 합니다.")
    val conditionReport: String? = null
)