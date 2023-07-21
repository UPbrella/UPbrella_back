package upbrella.be.store.StoreRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreMeta;

public interface StoreMetaRepository extends JpaRepository<StoreMeta, Long> {
}
