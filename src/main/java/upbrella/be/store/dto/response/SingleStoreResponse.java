package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SingleStoreResponse {

    private long id;
    private String name;
    private String classification;
    private boolean activateStatus;
    private String address;
    private String umbrellaLocation;
    private String businessHours;
    private String contactNumber;
    private String instagramId;
    private double latitude;
    private double longitude;
    private List<String> imageUrls;
}
