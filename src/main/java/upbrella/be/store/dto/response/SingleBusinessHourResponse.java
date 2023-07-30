package upbrella.be.store.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.BusinessHour;
import upbrella.be.store.entity.DayOfWeek;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class SingleBusinessHourResponse {

    private DayOfWeek date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;

    public static SingleBusinessHourResponse createSingleHourResponse(BusinessHour businessHour) {

            return SingleBusinessHourResponse.builder()
                    .date(businessHour.getDate())
                    .openAt(businessHour.getOpenAt())
                    .closeAt(businessHour.getCloseAt())
                    .build();
    }
}
