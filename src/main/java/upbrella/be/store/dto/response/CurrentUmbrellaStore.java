package upbrella.be.store.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUmbrellaStore {

    private long storeId;
    private String storeName;
}
