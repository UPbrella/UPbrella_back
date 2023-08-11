package upbrella.be.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.user.entity.BlackList;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {

    boolean existsBySocialId(Long socialId);
}
