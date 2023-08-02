package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.store.entity.StoreDetail;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SingleStoreResponse {

    private long id;
    private String name;
    private String category;
    private SingleClassificationResponse classification;
    private SingleSubClassificationResponse subClassification;
    private boolean activateStatus;
    private String address;
    private String addressDetail;
    private String thumbnail;
    private String umbrellaLocation;
    private String businessHour;
    private String contactNumber;
    private String instagramId;
    private double latitude;
    private double longitude;
    private String content;
    private List<SingleImageUrlResponse> imageUrls;
    private String password;
    private List<SingleBusinessHourResponse> businessHours;

    public static SingleStoreResponse ofCreateSingleStoreResponse(StoreDetail storeDetail, String thumbnail, List<SingleImageUrlResponse> images, List<SingleBusinessHourResponse> businessHours) {

        return SingleStoreResponse.builder()
                .id(storeDetail.getStoreMeta().getId())
                .name(storeDetail.getStoreMeta().getName())
                .category(storeDetail.getStoreMeta().getCategory())
                .classification(SingleClassificationResponse.ofCreateClassification(storeDetail.getStoreMeta().getClassification()))
                .subClassification(SingleSubClassificationResponse.ofCreateSubClassification(storeDetail.getStoreMeta().getSubClassification()))
                .activateStatus(storeDetail.getStoreMeta().isActivated())
                .address(storeDetail.getAddress())
                .addressDetail(storeDetail.getAddressDetail())
                .umbrellaLocation(storeDetail.getUmbrellaLocation())
                .businessHour(storeDetail.getWorkingHour())
                .contactNumber(storeDetail.getContactInfo())
                .instagramId(storeDetail.getInstaUrl())
                .latitude(storeDetail.getStoreMeta().getLongitude())
                .longitude(storeDetail.getStoreMeta().getLongitude())
                .content(storeDetail.getContent())
                .password(storeDetail.getStoreMeta().getPassword())
                .imageUrls(images)
                .thumbnail(thumbnail)
                .businessHours(businessHours)
                .build();
    }
}