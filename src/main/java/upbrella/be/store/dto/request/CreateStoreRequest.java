package upbrella.be.store.dto.request;


import lombok.*;
import upbrella.be.store.entity.Classification;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreRequest {

    private String name;
    private String category;
    private Classification classification;
    private Classification subClassification;
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
}
