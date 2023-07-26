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
public class SingleSubClassificationResponse {

    private long id;
    private String type;
    private String name;

    public SingleSubClassificationResponse(Classification classification) {
        this.id = classification.getId();
        this.type = classification.getType();
        this.name = classification.getName();
    }
}