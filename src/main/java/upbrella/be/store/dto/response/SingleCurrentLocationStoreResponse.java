package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SingleCurrentLocationStoreResponse {

    private int id;
    private String name;
    private boolean openStatus;
    private double latitude;
    private double longitude;
}
