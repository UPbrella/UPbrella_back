package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.BusinessHour;

import java.util.List;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

    List<BusinessHour> findByStoreMetaId(Long storeMetaId);
}
