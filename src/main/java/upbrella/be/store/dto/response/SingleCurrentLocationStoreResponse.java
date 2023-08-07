package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreMeta;

@Getter
@Builder
public class SingleCurrentLocationStoreResponse {

    private long id;
    private String name;
    private boolean openStatus;
    private double latitude;
    private double longitude;

    public static SingleCurrentLocationStoreResponse fromStoreMeta(boolean openStatus, StoreMeta storeMeta) {
        return SingleCurrentLocationStoreResponse.builder()
                .id(storeMeta.getId())
                .name(storeMeta.getName())
                .openStatus(openStatus)
                .latitude(storeMeta.getLatitude())
                .longitude(storeMeta.getLongitude())
                .build();
    }
}
