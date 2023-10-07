package upbrella.be.store.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.BusinessHour;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleBusinessHourResponse {

    private long id;
    private DayOfWeek date;
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openAt;
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeAt;

    public static SingleBusinessHourResponse createSingleHourResponse(BusinessHour businessHour) {

            return SingleBusinessHourResponse.builder()
                    .id(businessHour.getId())
                    .date(businessHour.getDate())
                    .openAt(businessHour.getOpenAt())
                    .closeAt(businessHour.getCloseAt())
                    .build();
    }
}
