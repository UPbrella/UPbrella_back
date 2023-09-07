package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.store.entity.StoreDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StoreFindByIdResponse {

    private long id;
    private String name;
    private String category;
    private long availableUmbrellaCount;
    private boolean openStatus;
    private String businessHours;
    private String contactNumber;
    private String instaUrl;
    private String address;
    private String umbrellaLocation;
    private String description;
    private double latitude;
    private double longitude;
    private List<String> imageUrls;

    public static StoreFindByIdResponse fromStoreDetail(StoreDetail storeDetail, long availableUmbrellaCount) {

        return StoreFindByIdResponse.builder()
                .id(storeDetail.getId())
                .name(storeDetail.getStoreMeta().getName())
                .businessHours(storeDetail.getWorkingHour())
                .contactNumber(storeDetail.getContactInfo())
                .address(storeDetail.getAddress())
                .instaUrl(storeDetail.getInstaUrl())
                .description(storeDetail.getContent())
                .category(storeDetail.getStoreMeta().getCategory())
                .umbrellaLocation(storeDetail.getUmbrellaLocation())
                .availableUmbrellaCount(availableUmbrellaCount)
                .openStatus(storeDetail.getStoreMeta().isOpenStore(LocalDateTime.now())) // 의사 결정 전까지 임시로 True 고정
                .latitude(storeDetail.getStoreMeta().getLatitude())
                .longitude(storeDetail.getStoreMeta().getLongitude())
                .imageUrls(storeDetail.getSortedStoreImages().stream()
                        .map(storeImage -> storeImage.getImageUrl())
                        .collect(Collectors.toList()))
                .build();
    }
}

