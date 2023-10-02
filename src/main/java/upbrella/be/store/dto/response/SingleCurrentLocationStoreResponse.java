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
    private long rentableUmbrellasCount;

    public static SingleCurrentLocationStoreResponse fromStoreMeta(boolean openStatus, StoreMetaWithUmbrellaCount storeMetaWithUmbrellaCount) {

        return SingleCurrentLocationStoreResponse.builder()
                .id(storeMetaWithUmbrellaCount.getStoreMeta().getId())
                .name(storeMetaWithUmbrellaCount.getStoreMeta().getName())
                .openStatus(openStatus)
                .latitude(storeMetaWithUmbrellaCount.getStoreMeta().getLatitude())
                .longitude(storeMetaWithUmbrellaCount.getStoreMeta().getLongitude())
                .rentableUmbrellasCount(storeMetaWithUmbrellaCount.getRentableUmbrellasCount())
                .build();
    }
}
