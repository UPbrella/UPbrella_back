package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.rent.entity.History;

import java.util.Optional;

public interface RentRepository extends JpaRepository<History, Long>, RentRepositoryCustom {

    Optional<History> findByUserIdAndReturnedAtIsNull(Long userId);

    long countByRentStoreMetaId(long storeId);

    long countAllByReturnedAtIsNotNullAndPaidAtIsNotNullAndRefundedAtIsNull();
}
