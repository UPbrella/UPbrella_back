package upbrella.be.store.dto.request;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@Setter
public class CoordinateRequest {

    private double latitudeFrom;
    private double latitudeTo;
    private double longitudeFrom;
    private double longitudeTo;
}
