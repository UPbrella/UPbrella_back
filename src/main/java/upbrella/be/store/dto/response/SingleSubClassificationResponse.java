package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleSubClassificationResponse {

    private long id;
    private String type;
    private String name;
}
