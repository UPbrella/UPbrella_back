package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreDetail;

import java.util.HashSet;
import java.util.Set;

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
    private String umbrellaLocation;
    private String businessHour;
    private String contactNumber;
    private String instagramId;
    private double latitude;
    private double longitude;
    private String content;
    private Set<SingleImageUrlResponse> imageUrls;
    private String password;
    private Set<SingleBusinessHourResponse> businessHours;

    public SingleStoreResponse(StoreDetail storeDetail) {

        imageUrls = new HashSet<>();
        businessHours = new HashSet<>();
        this.id = storeDetail.getStoreMeta().getId();
        this.name = storeDetail.getStoreMeta().getName();
        this.category = storeDetail.getStoreMeta().getCategory();
        this.classification = new SingleClassificationResponse(storeDetail.getStoreMeta().getClassification());
        this.subClassification = new SingleSubClassificationResponse(storeDetail.getStoreMeta().getSubClassification());
        this.activateStatus = storeDetail.getStoreMeta().isActivated();
        this.address = storeDetail.getAddress();
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
        storeDetail.getStoreMeta().getBusinessHours().forEach(businessHour -> {
            this.businessHours.add(SingleBusinessHourResponse.createSingleHourResponse(businessHour));
        });
    }
}