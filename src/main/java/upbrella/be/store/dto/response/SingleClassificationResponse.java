package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SingleClassificationResponse {

    private long id;
    private ClassificationType type;
    private String name;
    private double latitude;
    private double longitude;

    public SingleClassificationResponse(Classification classification) {
        this.id = classification.getId();
        this.type = classification.getType();
        this.name = classification.getName();
        this.latitude = classification.getLatitude();
        this.longitude = classification.getLongitude();
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