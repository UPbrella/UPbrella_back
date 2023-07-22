package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.store.entity.StoreImage;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
}
