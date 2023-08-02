package upbrella.be.store.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CoordinateRequest {

    private double latitudeFrom;
    private double latitudeTo;
    private double longitudeFrom;
    private double longitudeTo;
}
