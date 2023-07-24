package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinateRequest {

    @Range(min = -90, max = 90)
    private double latitude;
    @Range(min = -180, max = 180)
    private double longitude;
    private int zoomLevel;
}
