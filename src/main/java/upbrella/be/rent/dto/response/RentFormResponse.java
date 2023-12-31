package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
public class RentFormResponse {

    private String classificationName;
    private long storeMetaId;
    private String rentStoreName;
    private long umbrellaUuid;

    public static RentFormResponse of(Umbrella umbrella) {

        return RentFormResponse.builder()
                .classificationName(umbrella.getStoreMeta().getClassification().getName())
                .storeMetaId(umbrella.getStoreMeta().getId())
                .rentStoreName(umbrella.getStoreMeta().getName())
                .umbrellaUuid(umbrella.getUuid())
                .build();
    }
}
