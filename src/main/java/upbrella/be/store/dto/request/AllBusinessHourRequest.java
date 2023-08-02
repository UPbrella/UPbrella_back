package upbrella.be.store.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllBusinessHourRequest {

    private long storeMetaId;
    private List<SingleBusinessHourRequest> businessHours;

}
