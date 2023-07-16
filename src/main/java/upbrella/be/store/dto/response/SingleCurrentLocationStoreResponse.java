package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleCurrentLocationStoreResponse {

    private long id;
    private String name;
    private boolean openStatus;
    private double latitude;
    private double longitude;
}
