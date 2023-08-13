package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import upbrella.be.store.entity.StoreDetail;

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

    @QueryProjection
    public SingleStoreIntroductionResponse(long id, String thumbnail, String name, String category) {
        this.id = id;
        this.thumbnail = thumbnail;
        this.name = name;
        this.category = category;
    }
}
