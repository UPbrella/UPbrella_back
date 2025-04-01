package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import upbrella.be.store.entity.StoreImage;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {

    List<StoreImage> findByStoreDetailId(Long storeDetailId);
}