package upbrella.be.umbrella.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.umbrella.entity.Umbrella;

import java.util.List;
import java.util.Optional;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {

    Optional<Umbrella> findByIdAndDeletedIsFalse(long id);

    Optional<Umbrella> findByUuidAndDeletedIsFalse(long uuid);

    boolean existsByIdAndDeletedIsFalse(long id);

    boolean existsByUuidAndDeletedIsFalse(long uuid);

    List<Umbrella> findByDeletedIsFalseOrderById(Pageable pageable);

    List<Umbrella> findByStoreMetaIdAndDeletedIsFalseOrderById(long storeMetaId, Pageable pageable);

    int countUmbrellasByStoreMetaIdAndRentableIsTrueAndDeletedIsFalse(long storeMetaId);
}
