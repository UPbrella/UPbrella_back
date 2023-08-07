package upbrella.be.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SingleBusinessHourRequest {

    private DayOfWeek date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;
}
