package upbrella.be.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.rent.entity.History;

public interface RentRepository extends JpaRepository<History, Long> {

}
