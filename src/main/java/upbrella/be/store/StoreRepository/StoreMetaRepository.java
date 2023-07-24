package upbrella.be.store.StoreRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.Optional;

public interface StoreMetaRepository extends JpaRepository<StoreMeta, Long> {
    Optional<StoreMeta> findByIdAndDeletedIsFalse(long id);
    List<StoreMeta> findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo);
}
