package upbrella.be.store.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.DayOfWeek
import java.time.LocalTime

data class SingleBusinessHourRequest(
    val date: DayOfWeek,

    @field:JsonFormat(pattern = "HH:mm")
    val openAt: LocalTime,

    @field:JsonFormat(pattern = "HH:mm")
    val closeAt: LocalTime
)
