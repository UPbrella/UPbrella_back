package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.DayOfWeek;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleBusinessHourRequest {

    private long storeMetaId;
    private DayOfWeek date;
    private String openAt;
    private String closeAy;
}
