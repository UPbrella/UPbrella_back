package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllStoreIntroductionResponse {


    private List<StoreIntroductionsResponseByClassification> storesByClassification;

    public static AllStoreIntroductionResponse of(List<StoreIntroductionsResponseByClassification> storesByClassification) {

        return AllStoreIntroductionResponse.builder()
                .storesByClassification(storesByClassification)
                .build();
    }
}
