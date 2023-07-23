package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleClassificationResponse {

    private long id;
    private String type;
    private String name;
    private double latitude;
    private double longitude;
}
