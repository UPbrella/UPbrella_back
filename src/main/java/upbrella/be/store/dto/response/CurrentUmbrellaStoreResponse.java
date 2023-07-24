package upbrella.be.store.dto.response;

import lombok.*;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
