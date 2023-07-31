package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.BusinessHour;

public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {
}
