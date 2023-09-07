package upbrella.be.rent.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.rent.entity.History;
import upbrella.be.store.entity.StoreMeta;

@Getter
@Builder
public class ReturnFormResponse {

    private String classificationName;
    private String rentStoreName;
    private long storeId;

    public static ReturnFormResponse of(StoreMeta storeMeta, History history) {

        return ReturnFormResponse.builder()
                .classificationName(storeMeta.getClassification().getName())
                .rentStoreName(history.getRentStoreMeta().getName())
                .storeId(storeMeta.getId())
                .build();
    }
}
