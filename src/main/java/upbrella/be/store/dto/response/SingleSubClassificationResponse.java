package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SingleSubClassificationResponse {

    private long id;
    private ClassificationType type;
    private String name;

    public static SingleSubClassificationResponse ofCreateSubClassification(Classification classification) {

        return SingleSubClassificationResponse.builder()
                .id(classification.getId())
                .type(classification.getType())
                .name(classification.getName())
                .build();
    }
}