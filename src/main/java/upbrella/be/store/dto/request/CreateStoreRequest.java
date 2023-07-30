package upbrella.be.store.dto.request;


import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String category;
    private long classificationId;
    private long subClassificationId;
    private boolean activateStatus;
    @NotBlank
    private String address;
    @NotBlank
    private String umbrellaLocation;
    @NotBlank
    private String businessHours;
    private String contactNumber;
    private String instagramId;
    @Range(min = -90, max = 90)
    private double latitude;
    @Range(min = -180, max = 180)
    private double longitude;
    private String content;
    @Range(min = 0, max = 10)
    @NotBlank
    private List<String> imageUrls;
    private String password;
}