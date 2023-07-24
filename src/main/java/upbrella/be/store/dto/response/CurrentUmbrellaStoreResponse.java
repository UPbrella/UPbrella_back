package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;
import upbrella.be.umbrella.entity.Umbrella;

@Getter
@Builder
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
