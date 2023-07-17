package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUmbrellaStoreResponse {

    private int id;
    private String name;
}
