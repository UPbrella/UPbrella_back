package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllImageUrlResponse {

    private Long storeId;
    private List<SingleImageUrlResponse> images;

    public static AllImageUrlResponse of(Long storeId, List<SingleImageUrlResponse> images) {
        return AllImageUrlResponse.builder()
                .storeId(storeId)
                .images(images)
                .build();
    }
}
