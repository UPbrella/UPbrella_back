package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.Classification;

public interface ClassificationRepository extends JpaRepository<Classification, Long> {
}
