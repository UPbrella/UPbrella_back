package upbrella.be.umbrella.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaResponse {

    private long id;
    private Long historyId;
    private long storeMetaId;
    private String storeName;
    private long uuid;
    private boolean rentable;

    public static UmbrellaResponse fromUmbrella(UmbrellaWithHistory umbrellaWithHistory) {

        return UmbrellaResponse.builder()
                .id(umbrellaWithHistory.getId())
                .historyId(umbrellaWithHistory.getHistoryId())
                .rentable(umbrellaWithHistory.isRentable())
                .storeMetaId(umbrellaWithHistory.getStoreMeta().getId())
                .storeName(umbrellaWithHistory.getStoreMeta().getName())
                .uuid(umbrellaWithHistory.getUuid())
                .build();
    }
}
