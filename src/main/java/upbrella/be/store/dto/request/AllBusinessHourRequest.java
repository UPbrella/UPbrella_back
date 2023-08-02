package upbrella.be.store.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AllBusinessHourRequest {

    private long storeMetaId;
    private List<SingleBusinessHourRequest> businessHours;

}
