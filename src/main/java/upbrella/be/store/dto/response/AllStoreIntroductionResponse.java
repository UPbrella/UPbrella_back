package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllStoreIntroductionResponse {

    private List<SingleStoreIntroductionResponse> stores;

    public static AllStoreIntroductionResponse of(List<SingleStoreIntroductionResponse> stores) {

        return AllStoreIntroductionResponse.builder()
                .stores(stores)
                .build();
    }
}
