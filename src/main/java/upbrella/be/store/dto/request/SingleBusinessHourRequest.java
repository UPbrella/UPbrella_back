package upbrella.be.store.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.DayOfWeek;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleBusinessHourRequest {

    private DayOfWeek date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;
}
