package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import upbrella.be.store.entity.Classification;

import java.util.List;

public interface ClassificationRepository extends JpaRepository<Classification, Long> {

    @Query("select c from Classification c where c.type = :type")
    List<Classification> findAllByClassification(String type);
}
