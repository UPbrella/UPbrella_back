package upbrella.be.umbrella.repository;

import org.springframework.data.domain.Pageable;
import upbrella.be.umbrella.dto.response.UmbrellaWithHistory;

import java.util.List;

public interface UmbrellaRepositoryCustom {

    long countAllUmbrellas();

    long countRentableUmbrellas();

    long countRentedUmbrellas();

    long countMissingUmbrellas();

    long countRentableUmbrellasByStore(long storeMetaId);

    long countRentedUmbrellasByStore(long storeMetaId);

    long countAllUmbrellasByStore(long storeId);

    long countMissingUmbrellasByStore(long storeId);

    List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaId(Pageable pageable);

    List<UmbrellaWithHistory> findUmbrellaAndHistoryOrderedByUmbrellaIdByStoreId(long storeId, Pageable pageable);
}
