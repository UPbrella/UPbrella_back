package upbrella.be.umbrella.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.umbrella.entity.Umbrella;

import java.util.List;
import java.util.Optional;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {

    Optional<Umbrella> findByIdAndDeletedIsFalse(long id);

    boolean existsByIdAndDeletedIsFalse(long id);

    boolean existsByUuidAndDeletedIsFalse(long uuid);

    List<Umbrella> findByDeletedIsFalseOrderById(Pageable pageable);

    List<Umbrella> findByStoreMetaIdAndDeletedIsFalseOrderById(long storeMetaId, Pageable pageable);

    // 대여가능 우산 조회
    int countUmbrellasByRentableIsTrueAndMissedIsFalseAndDeletedIsFalse();

    // 대여중인 우산 조회
    int countUmbrellasByRentableIsFalseAndMissedIsFalseAndDeletedIsFalse();

    // 전체 우산 조회
    int countUmbrellaByDeletedIsFalse();

    // 분실 우산 조회
    int countUmbrellasByMissedIsTrueAndDeletedIsFalse();

    // 지점 대여 가능 우산 조회
    int countUmbrellasByStoreMetaIdAndRentableIsTrueAndMissedIsFalseAndDeletedIsFalse(long storeMetaId);

    // 지점 대여중인 우산 조회
    int countUmbrellasByStoreMetaIdAndRentableIsFalseAndMissedIsFalseAndDeletedIsFalse(long storeMetaId);

    // 지점 전체 우산 조회
    int countUmbrellaByStoreMetaIdAndDeletedIsFalse(long storeId);

    // 지점 분실 우산 조회
    int countUmbrellasByStoreMetaIdAndMissedIsTrueAndDeletedIsFalse(long storeId);
}
