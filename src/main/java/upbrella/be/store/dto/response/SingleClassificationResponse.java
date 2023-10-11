package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingleClassificationResponse {

    private long id;
    private ClassificationType type;
    private String name;
    private double latitude;
    private double longitude;

    @QueryProjection
    public SingleClassificationResponse(long id, ClassificationType type, String name, double latitude, double longitude) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static SingleClassificationResponse ofCreateClassification(Classification classification) {

        return SingleClassificationResponse.builder()
                .id(classification.getId())
                .type(classification.getType())
                .name(classification.getName())
                .latitude(classification.getLatitude())
                .longitude(classification.getLongitude())
                .build();
    }
}