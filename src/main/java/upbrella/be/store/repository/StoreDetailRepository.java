package upbrella.be.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import upbrella.be.store.entity.StoreDetail;

import java.util.List;

public interface StoreDetailRepository extends JpaRepository<StoreDetail, Long> {

    @Query("select sd from StoreDetail sd join fetch sd.storeImages image join fetch sd.storeMeta meta")
    public List<StoreDetail> findAll();
}
