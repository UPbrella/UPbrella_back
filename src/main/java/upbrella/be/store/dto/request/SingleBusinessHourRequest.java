package upbrella.be.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import upbrella.be.store.entity.DayOfWeek;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleBusinessHourRequest {

    private DayOfWeek date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;
}
