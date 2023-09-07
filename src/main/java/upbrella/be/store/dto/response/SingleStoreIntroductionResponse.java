package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

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
}
