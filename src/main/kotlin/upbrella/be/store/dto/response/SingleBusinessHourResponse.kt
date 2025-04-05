package upbrella.be.store.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import java.time.DayOfWeek
import java.time.LocalTime
import upbrella.be.store.entity.BusinessHour

data class SingleBusinessHourResponse(
    val id: Long?,
    val date: DayOfWeek,
    @field:JsonSerialize(using = LocalTimeSerializer::class)
    @field:JsonDeserialize(using = LocalTimeDeserializer::class)
    @field:JsonFormat(pattern = "HH:mm")
    val openAt: LocalTime?,
    @field:JsonSerialize(using = LocalTimeSerializer::class)
    @field:JsonDeserialize(using = LocalTimeDeserializer::class)
    @field:JsonFormat(pattern = "HH:mm")
    val closeAt: LocalTime?
) {
    companion object {
        fun createSingleHourResponse(businessHour: BusinessHour): SingleBusinessHourResponse {
            return SingleBusinessHourResponse(
                id = businessHour.id,
                date = businessHour.date,
                openAt = businessHour.openAt,
                closeAt = businessHour.closeAt
            )
        }
    }
}
