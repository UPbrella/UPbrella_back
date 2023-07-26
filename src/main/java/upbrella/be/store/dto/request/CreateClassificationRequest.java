package upbrella.be.store.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClassificationRequest {

    @NotBlank
    private String type;
    @NotBlank
    private String name;
    @Range(min = -90, max = 90)
    private double latitude;
    @Range(min = -180, max = 180)
    private double longitude;
}
