package upbrella.be.store.dto.request

import javax.validation.constraints.NotBlank

data class CreateSubClassificationRequest(
    @field:NotBlank
    val name: String
)
