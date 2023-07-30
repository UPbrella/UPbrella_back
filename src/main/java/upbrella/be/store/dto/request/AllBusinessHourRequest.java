package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllBusinessHourRequest {

    private long storeMetaId;
    private List<SingleBusinessHourRequest> businessHours;

}
