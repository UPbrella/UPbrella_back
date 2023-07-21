package upbrella.be.umbrella.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.umbrella.entity.Umbrella;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {
    boolean existsByUuid(long uuid);
}
