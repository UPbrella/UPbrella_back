package upbrella.be.store.dto.request

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank

data class CreateClassificationRequest(

    @field:NotBlank
    val name: String,

    @field:Range(min = -90, max = 90)
    val latitude: Double,

    @field:Range(min = -180, max = 180)
    val longitude: Double
)
