package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.Classification;

import java.util.List;

public interface ClassificationRepository extends JpaRepository<Classification, Long> {

    List<Classification> findByType(String type);
}
