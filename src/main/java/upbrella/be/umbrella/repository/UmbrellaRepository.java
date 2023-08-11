package upbrella.be.umbrella.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.umbrella.entity.Umbrella;

import java.util.List;
import java.util.Optional;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long>, UmbrellaRepositoryCustom {

    Optional<Umbrella> findByIdAndDeletedIsFalse(long id);

    boolean existsByIdAndDeletedIsFalse(long id);

    boolean existsByUuidAndDeletedIsFalse(long uuid);

    List<Umbrella> findByDeletedIsFalseOrderById(Pageable pageable);

    List<Umbrella> findByStoreMetaIdAndDeletedIsFalseOrderById(long storeMetaId, Pageable pageable);


}
