package upbrella.be.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import upbrella.be.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByNameAndPhoneNumber(String userName, String phoneNumber);
}
