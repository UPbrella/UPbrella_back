package upbrella.be.user.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class JoinRequest(
    @field:Size(min = 2, max = 20)
    @field:Pattern(regexp = "^[가-힣a-zA-Z\\s]{2,20}$", message = "이름은 한글, 영문, 공백 2~20자로 입력해주세요.")
    val name: String = "",

    @field:Size(max = 16)
    @field:Pattern(regexp = "^\\d{3}-?\\d{4}-?\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    val phoneNumber: String? = null,

    @field:Email
    @field:Size(max = 100)
    val email: String? = null,

    @field:Size(max = 40)
    val bank: String? = null,

    @field:Size(max = 40)
    val accountNumber: String? = null
)
