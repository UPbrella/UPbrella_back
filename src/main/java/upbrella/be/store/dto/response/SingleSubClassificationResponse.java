package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import upbrella.be.store.entity.Classification;
import upbrella.be.store.entity.ClassificationType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleSubClassificationResponse {

    private long id;
    private ClassificationType type;
    private String name;

    @QueryProjection
    public SingleSubClassificationResponse(long id, ClassificationType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public static SingleSubClassificationResponse ofCreateSubClassification(Classification classification) {

        return SingleSubClassificationResponse.builder()
                .id(classification.getId())
                .type(classification.getType())
                .name(classification.getName())
                .build();
    }
}