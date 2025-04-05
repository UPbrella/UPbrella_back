package upbrella.be.user.dto.request

import upbrella.be.user.validation.OnlyNumbers
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class UpdateBankAccountRequest(
    @field:Size(min = 1, max = 10)
    @field:NotBlank
    val bank: String,

    @field:Size(min = 1, max = 45)
    @field:NotBlank
    @field:OnlyNumbers
    val accountNumber: String
)