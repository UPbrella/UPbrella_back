package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.Classification;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleClassificationResponse {

    private long id;
    private String type;
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