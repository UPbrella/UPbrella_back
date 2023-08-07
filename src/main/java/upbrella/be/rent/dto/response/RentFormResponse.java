package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RentFormResponse {

    private String classificationName;
    private String rentStoreName;
    private long umbrellaUuid;
    private long password;
}
