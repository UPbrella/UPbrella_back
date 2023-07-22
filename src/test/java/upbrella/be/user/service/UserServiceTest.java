package upbrella.be.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.login.dto.response.NaverLoggedInUser;
import upbrella.be.login.dto.response.LoggedInUserResponse;
import upbrella.be.user.entity.User;
import upbrella.be.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @TestFactory
    @DisplayName("신규 회원은 새로 회원가입할 수 있다.")
    Collection<DynamicTest> test() {
        // given
        NaverLoggedInUser user = NaverLoggedInUser.builder()
                .name("이름")
                .mobile("010-1234-5678")
                .build();

        // when

        // then

        return List.of(
                DynamicTest.dynamicTest("새로 로그인한 유저는 DB에 저장되지 않았다.", () -> {
                    Optional<User> foundUser = userRepository.findByNameAndPhoneNumber(user.getName(), user.getMobile());
                    assertTrue(foundUser.isEmpty());
                }),
                DynamicTest.dynamicTest("새로 로그인한 유저는 DB에 저장된다.", () -> {
                    LoggedInUserResponse loggedInUserResponse = userService.joinService(user.getName(), user.getMobile());
                    Optional<User> foundUser = userRepository.findByNameAndPhoneNumber(user.getName(), user.getMobile());
                    assertTrue(foundUser.isPresent());
                    assertEquals(loggedInUserResponse.getId(), foundUser.get().getId());
                    assertEquals(loggedInUserResponse.getName(), foundUser.get().getName());
                    assertEquals(loggedInUserResponse.getPhoneNumber(), foundUser.get().getPhoneNumber());
                    assertEquals(loggedInUserResponse.isAdminStatus(), foundUser.get().isAdminStatus());
                })
        );

    }
}