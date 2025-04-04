package upbrella.be.user.dto.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class JoinRequest(
    @field:NotBlank
    @field:Size(max = 6)
    val name: String = "",

    @field:NotBlank
    @field:Size(max = 16)
    @field:Pattern(regexp = "^\\d{3}-?\\d{4}-?\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    val phoneNumber: String = "",

    @field:Size(max = 40)
    val bank: String? = null,

    @field:Size(max = 40)
    val accountNumber: String? = null
)
