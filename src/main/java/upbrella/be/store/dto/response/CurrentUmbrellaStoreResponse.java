package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CurrentUmbrellaStoreResponse {

    private long id;
    private String name;

    public static CurrentUmbrellaStoreResponse fromUmbrella(Umbrella umbrella) {

        return CurrentUmbrellaStoreResponse.builder()
                .id(umbrella.getStoreMeta().getId())
                .name(umbrella.getStoreMeta().getName())
                .build();
    }
}
