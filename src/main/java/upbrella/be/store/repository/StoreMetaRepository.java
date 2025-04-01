package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import upbrella.be.store.entity.StoreMeta;

public interface StoreMetaRepository extends JpaRepository<StoreMeta, Long>, StoreMetaRepositoryCustom {

    Optional<StoreMeta> findByClassificationIdAndDeletedIsFalse(long id);
}
