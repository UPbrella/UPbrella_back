package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
public class RentFormResponse {

    private String classificationName;
    private String rentStoreName;
    private long umbrellaUuid;
    private String password;

    public static RentFormResponse of(Umbrella umbrella) {

        return RentFormResponse.builder()
                .classificationName(umbrella.getStoreMeta().getClassification().getName())
                .rentStoreName(umbrella.getStoreMeta().getName())
                .umbrellaUuid(umbrella.getUuid())
                .password(umbrella.getStoreMeta().getPassword())
                .build();
    }
}
