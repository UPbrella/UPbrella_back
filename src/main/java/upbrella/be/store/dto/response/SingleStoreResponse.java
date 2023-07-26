package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;

import java.util.ArrayList;
import java.util.List;

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
    private String businessHours;
    private String contactNumber;
    private String instagramId;
    private double latitude;
    private double longitude;
    private String content;
    private List<String> imageUrls;

    public SingleStoreResponse(StoreDetail storeDetail) {
        imageUrls = new ArrayList<>();
        this.id = storeDetail.getStoreMeta().getId();
        this.name = storeDetail.getStoreMeta().getName();
        this.category = storeDetail.getStoreMeta().getCategory();
        this.classification = new SingleClassificationResponse(storeDetail.getStoreMeta().getClassification());
        this.subClassification = new SingleSubClassificationResponse(storeDetail.getStoreMeta().getSubClassification());
        this.activateStatus = storeDetail.getStoreMeta().isActivated();
        this.address = storeDetail.getAddress();
        this.umbrellaLocation = storeDetail.getUmbrellaLocation();
        this.businessHours = storeDetail.getWorkingHour();
        this.contactNumber = storeDetail.getContactInfo();
        this.instagramId = storeDetail.getInstaUrl();
        this.latitude = storeDetail.getStoreMeta().getLongitude();
        this.longitude = storeDetail.getStoreMeta().getLongitude();
        this.content = storeDetail.getContent();
        for (StoreImage imageUrl : storeDetail.getStoreImages()) {
            imageUrls.add(imageUrl.getImageUrl());
        }
    }
}