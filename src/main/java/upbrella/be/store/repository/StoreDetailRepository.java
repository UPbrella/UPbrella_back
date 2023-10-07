package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreDetail;

import java.util.Optional;


public interface StoreDetailRepository extends JpaRepository<StoreDetail, Long>, StoreDetailRepositoryCustom {

    Optional<StoreDetail> findStoreDetailByStoreMetaId(long storeMetaId);
}
