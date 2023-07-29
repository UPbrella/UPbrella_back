package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
public class UmbrellaResponse {

    private long id;
    private long storeMetaId;
    private long uuid;
    private boolean rentable;

    public static UmbrellaResponse fromUmbrella(Umbrella umbrella) {
        return UmbrellaResponse.builder()
                .id(umbrella.getId())
                .rentable(umbrella.isRentable())
                .storeMetaId(umbrella.getStoreMeta().getId())
                .uuid(umbrella.getUuid())
                .build();
    }
}