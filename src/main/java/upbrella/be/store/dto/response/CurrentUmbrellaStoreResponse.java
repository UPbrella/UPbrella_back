package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUmbrellaStoreResponse {

    private long id;
    private String name;
}
