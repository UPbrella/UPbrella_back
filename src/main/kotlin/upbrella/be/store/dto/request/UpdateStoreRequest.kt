package upbrella.be.store.dto.request

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank

data class UpdateStoreRequest(

    @field:NotBlank
    val name: String,

    @field:NotBlank
    val category: String,

    val classificationId: Long,
    val subClassificationId: Long,

    @field:NotBlank
    val address: String,

    @field:NotBlank
    val addressDetail: String,

    @field:NotBlank
    val umbrellaLocation: String,

    @field:NotBlank
    val businessHour: String,

    val contactNumber: String?,
    val instagramId: String?,

    @field:Range(min = -90, max = 90)
    val latitude: Double,

    @field:Range(min = -180, max = 180)
    val longitude: Double,

    val content: String?,
    val businessHours: List<SingleBusinessHourRequest>
)
