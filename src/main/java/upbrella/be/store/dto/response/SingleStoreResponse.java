package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreImage;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
//@Builder
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

    @QueryProjection
    public SingleStoreResponse(long id, String name, String category, SingleClassificationResponse classification, SingleSubClassificationResponse subClassification, boolean activateStatus, String address, String umbrellaLocation, String businessHours, String contactNumber, String instagramId, double latitude, double longitude, String content, List<StoreImage> storeImages) {
        imageUrls = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.category = category;
        this.classification = classification;
        this.subClassification = subClassification;
        this.activateStatus = activateStatus;
        this.address = address;
        this.umbrellaLocation = umbrellaLocation;
        this.businessHours = businessHours;
        this.contactNumber = contactNumber;
        this.instagramId = instagramId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.content = content;
        for (StoreImage imageUrl : storeImages) {
            imageUrls.add(imageUrl.getImageUrl());
        }
    }
}
