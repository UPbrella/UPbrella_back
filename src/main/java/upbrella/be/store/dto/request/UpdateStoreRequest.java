package upbrella.be.store.dto.request;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateStoreRequest {

    @NotBlank
    private String name;
    @NotBlank
    private String category;
    private long classificationId;
    private long subClassificationId;
    @NotBlank
    private String address;
    @NotBlank
    private String addressDetail;
    @NotBlank
    private String umbrellaLocation;
    @NotBlank
    private String businessHour;
    private String contactNumber;
    private String instagramId;
    @Range(min = -90, max = 90)
    private double latitude;
    @Range(min = -180, max = 180)
    private double longitude;
    private String content;
    private List<SingleBusinessHourRequest> businessHours;
}
