package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreDetail;

import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public SingleStoreResponse(StoreDetail storeDetail) {

        imageUrls = new ArrayList<>();
        businessHours = new ArrayList<>();
        this.id = storeDetail.getStoreMeta().getId();
        this.name = storeDetail.getStoreMeta().getName();
        this.category = storeDetail.getStoreMeta().getCategory();
        this.classification = new SingleClassificationResponse(storeDetail.getStoreMeta().getClassification());
        this.subClassification = new SingleSubClassificationResponse(storeDetail.getStoreMeta().getSubClassification());
        this.activateStatus = storeDetail.getStoreMeta().isActivated();
        this.address = storeDetail.getAddress();
        this.addressDetail = storeDetail.getAddressDetail();
        this.umbrellaLocation = storeDetail.getUmbrellaLocation();
        this.businessHour = storeDetail.getWorkingHour();
        this.contactNumber = storeDetail.getContactInfo();
        this.instagramId = storeDetail.getInstaUrl();
        this.latitude = storeDetail.getStoreMeta().getLongitude();
        this.longitude = storeDetail.getStoreMeta().getLongitude();
        this.content = storeDetail.getContent();
        this.password = storeDetail.getStoreMeta().getPassword();
        storeDetail.getStoreImages().forEach(imageUrl -> {
            this.imageUrls.add(SingleImageUrlResponse.createImageUrlResponse(imageUrl));
        });
        this.imageUrls.sort(Comparator.comparing(SingleImageUrlResponse::getId));
        this.thumbnail = imageUrls.stream().findFirst().map(SingleImageUrlResponse::getImageUrl).orElse(null);
        storeDetail.getStoreMeta().getBusinessHours().forEach(businessHour -> {
            this.businessHours.add(SingleBusinessHourResponse.createSingleHourResponse(businessHour));
        });
        this.businessHours.sort(Comparator.comparing(SingleBusinessHourResponse::getDate));
    }
}