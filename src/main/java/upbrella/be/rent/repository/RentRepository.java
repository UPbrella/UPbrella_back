package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import upbrella.be.rent.entity.History;

public interface RentRepository extends JpaRepository<History, Long>, RentRepositoryCustom {

    Optional<History> findByUserIdAndReturnedAtIsNull(Long userId);

    long countByRentStoreMetaId(long storeId);

    long countAllByReturnedAtIsNotNullAndPaidAtIsNotNullAndRefundedAtIsNull();
}
