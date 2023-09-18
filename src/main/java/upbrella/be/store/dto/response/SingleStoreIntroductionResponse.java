package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SingleStoreIntroductionResponse {

    private long id;
    private String thumbnail;
    private String name;
    private String category;

    public static SingleStoreIntroductionResponse of(long id, String thumbnail, String name, String category) {

        return SingleStoreIntroductionResponse.builder()
                .id(id)
                .thumbnail(thumbnail)
                .name(name)
                .category(category)
                .build();
    }
    public static SingleStoreIntroductionResponse createSingleIntroduction(StoreDetail storeDetail) {

        List<SingleImageUrlResponse> sortedImageUrls = storeDetail.getSortedStoreImages().stream()
                .map(SingleImageUrlResponse::createImageUrlResponse)
                .collect(Collectors.toList());

        String thumbnail = createThumbnail(sortedImageUrls);
        StoreMeta storeMeta = storeDetail.getStoreMeta();

        return of(storeMeta.getId(), thumbnail, storeMeta.getName(), storeMeta.getCategory());
    }

    private static String createThumbnail(List<SingleImageUrlResponse> imageUrls) {

        return imageUrls.stream()
                .findFirst()
                .map(SingleImageUrlResponse::getImageUrl)
                .orElse(null);
    }
}
