package upbrella.be.rent.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class RentUmbrellaByUserRequest(
    val region: String? = null,
    val storeId: Long = 0,
    val umbrellaId: Long = 0,

    @field:Size(max = 400, message = "conditionReport는 최대 400자여야 합니다.")
    val conditionReport: String? = null,

    @field:NotBlank(message = "전화번호는 필수입니다.")
    @field:Size(max = 16)
    @field:Pattern(regexp = "^\\d{3}-?\\d{4}-?\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    val phoneNumber: String = ""
)