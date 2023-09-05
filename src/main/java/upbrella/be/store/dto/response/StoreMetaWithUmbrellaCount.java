package upbrella.be.store.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import upbrella.be.store.entity.StoreMeta;

@Getter
@NoArgsConstructor
public class StoreMetaWithUmbrellaCount {

    private StoreMeta storeMeta;
    private long rentableUmbrellasCount;

    @QueryProjection
    public StoreMetaWithUmbrellaCount(StoreMeta storeMeta, long rentableUmbrellasCount) {

        this.storeMeta = storeMeta;
        this.rentableUmbrellasCount = rentableUmbrellasCount;
    }
}
