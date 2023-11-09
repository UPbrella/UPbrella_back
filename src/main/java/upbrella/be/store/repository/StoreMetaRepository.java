package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.Optional;

public interface StoreMetaRepository extends JpaRepository<StoreMeta, Long>, StoreMetaRepositoryCustom {

    Optional<StoreMeta> findByClassificationIdAndDeletedIsFalse(long id);
}
