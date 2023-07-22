package upbrella.be.umbrella.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.umbrella.entity.Umbrella;

import java.util.Optional;

public interface UmbrellaRepository extends JpaRepository<Umbrella, Long> {

    Optional<Umbrella> findByIdAndDeletedIsFalse(long id);
    boolean existsByIdAndDeletedIsFalse(long id);
    boolean existsByUuidAndDeletedIsFalse(long uuid);
}
