package upbrella.be.store.dto.request;


import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreRequest {

    private String name;
    private String classification;
    private boolean activateStatus;
    private String address;
    private String umbrellaLocation;
    private String businessHours;
    private String contactNumber;
    private String instagramId;
    private String coordinate; //  좌표를 줘서 프론에서 네이버 지도 api를 이용해서 좌표로 지도를 띄워줄 수 있도록
    private List<String> imageUrls;
}
