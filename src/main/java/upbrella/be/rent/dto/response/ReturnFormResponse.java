package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreMeta;

@Getter
@Builder
public class ReturnFormResponse {

    private String classificationName;
    private long storeId;
    private String storeName;

    public static ReturnFormResponse of(StoreMeta storeMeta) {

        return ReturnFormResponse.builder()
                .classificationName(storeMeta.getClassification().getName())
                .storeId(storeMeta.getId())
                .storeName(storeMeta.getName())
                .build();
    }
}
