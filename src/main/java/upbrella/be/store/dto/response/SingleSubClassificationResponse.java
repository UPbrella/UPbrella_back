package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleSubClassificationResponse {

    private long id;
    private ClassificationType type;
    private String name;

    public SingleSubClassificationResponse(Classification classification) {
        this.id = classification.getId();
        this.type = classification.getType();
        this.name = classification.getName();
    }

    public static SingleSubClassificationResponse ofCreateSubClassification(Classification classification) {
        return SingleSubClassificationResponse.builder()
                .id(classification.getId())
                .type(classification.getType())
                .name(classification.getName())
                .build();
    }
}