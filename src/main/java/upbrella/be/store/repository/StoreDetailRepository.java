package upbrella.be.store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreDetail;


public interface StoreDetailRepository extends JpaRepository<StoreDetail, Long>, StoreDetailRepositoryCustom {

    Optional<StoreDetail> findStoreDetailByStoreMetaId(long storeMetaId);
}
