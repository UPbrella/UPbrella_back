package upbrella.be.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public LoggedInUserResponse joinService(String userName, String phoneNumber) {

        Optional<User> foundUser = userRepository.findByNameAndPhoneNumber(userName, phoneNumber);

        if (foundUser.isEmpty()) {
            User createdUser = userRepository.save(User.createNewUser(userName, phoneNumber));
            return LoggedInUserResponse.loggedInUser(createdUser);
        }

        return LoggedInUserResponse.loggedInUser(foundUser.get());
    }

    public long findUmbrellaBorrowedByUser(long userId) {
        return 1;
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 유저 고유번호입니다."));
    }
}
