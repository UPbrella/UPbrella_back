package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreMeta;

import java.util.List;
import java.util.Optional;

public interface StoreMetaRepository extends JpaRepository<StoreMeta, Long>, StoreMetaRepositoryCustom {
    Optional<StoreMeta> findByIdAndDeletedIsFalse(long id);
    List<StoreMeta> findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo);
}
