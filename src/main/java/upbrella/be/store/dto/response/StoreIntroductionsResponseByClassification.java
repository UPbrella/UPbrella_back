package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreDetail;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class StoreIntroductionsResponseByClassification {

    private long subClassificationId;
    private List<SingleStoreIntroductionResponse> stores;

    public static StoreIntroductionsResponseByClassification of(long subClassificationId, List<StoreDetail> storeDetails) {

        return StoreIntroductionsResponseByClassification.builder()
                .subClassificationId(subClassificationId)
                .stores(storeDetails.stream()
                                .map(SingleStoreIntroductionResponse::createSingleIntroduction)
                                .collect(Collectors.toList()))
                .build();
    }
}
