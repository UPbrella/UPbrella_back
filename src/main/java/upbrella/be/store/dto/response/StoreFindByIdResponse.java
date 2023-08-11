package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.store.entity.StoreDetail;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StoreFindByIdResponse {

    private long id;
    private String name;
    private String businessHours;
    private String contactNumber;
    private String address;
    private long availableUmbrellaCount;
    private boolean openStatus;
    private double latitude;
    private double longitude;

    public static StoreFindByIdResponse fromStoreDetail(StoreDetail storeDetail, long availableUmbrellaCount) {

        return StoreFindByIdResponse.builder()
                .id(storeDetail.getId())
                .name(storeDetail.getStoreMeta().getName())
                .businessHours(storeDetail.getWorkingHour())
                .contactNumber(storeDetail.getContactInfo())
                .address(storeDetail.getAddress())
                .availableUmbrellaCount(availableUmbrellaCount)
                .openStatus(true) // 의사 결정 전까지 임시로 True 고정
                .latitude(storeDetail.getStoreMeta().getLatitude())
                .longitude(storeDetail.getStoreMeta().getLongitude())
                .build();
    }
}

