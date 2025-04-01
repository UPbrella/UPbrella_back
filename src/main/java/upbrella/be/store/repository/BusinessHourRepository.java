package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import upbrella.be.store.entity.BusinessHour;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

    List<BusinessHour> findByStoreMetaId(Long storeMetaId);

    void deleteAllByStoreMetaId(Long storeMetaId);
}
