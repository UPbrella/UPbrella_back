package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SingleStoreResponse implements Serializable {

    private long id;
    private String name;
    private String category;
    private SingleClassificationResponse classification;
    private SingleSubClassificationResponse subClassification;
    private boolean activateStatus;
    private String address;
    private String addressDetail;
    private String umbrellaLocation;
    private String businessHour;
    private String contactNumber;
    private String instagramId;
    private double latitude;
    private double longitude;
    private String content;

    @QueryProjection
    public SingleStoreResponse(long id, String name, String category, SingleClassificationResponse classification, SingleSubClassificationResponse subClassification, boolean activateStatus, String address, String addressDetail, String umbrellaLocation, String businessHour, String contactNumber, String instagramId, double latitude, double longitude, String content) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.classification = classification;
        this.subClassification = subClassification;
        this.activateStatus = activateStatus;
        this.address = address;
        this.addressDetail = addressDetail;
        this.umbrellaLocation = umbrellaLocation;
        this.businessHour = businessHour;
        this.contactNumber = contactNumber;
        this.instagramId = instagramId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.content = content;
    }
}
