package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUmbrellaStoreReponse {

    private int storeId;
    private String storeName;
}
