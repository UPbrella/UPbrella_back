package upbrella.be.store.repository;

import upbrella.be.store.entity.StoreMeta;

import java.util.List;

public interface StoreMetaRepositoryCustom {

    List<StoreMeta> findAllByDeletedIsFalseAndLatitudeBetweenAndLongitudeBetween(double latitudeFrom, double latitudeTo, double longitudeFrom, double longitudeTo);
}
