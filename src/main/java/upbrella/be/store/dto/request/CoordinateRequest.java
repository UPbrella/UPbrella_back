package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinateRequest {

    private double latitude;
    private double longitude;
    private int zoomLevel;
}
