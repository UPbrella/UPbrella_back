package upbrella.be.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreImage;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class AllImageUrlResponse {

    private List<SingleImageUrlResponse> singleImageUrlResponses;

    public static AllImageUrlResponse fromImagesWithSorted(List<StoreImage> images) {

        return AllImageUrlResponse.builder()
                .singleImageUrlResponses(images.stream()
                        .map(SingleImageUrlResponse::createImageUrlResponse)
                        .collect(Collectors.toList()))
                .build();
    }
}
