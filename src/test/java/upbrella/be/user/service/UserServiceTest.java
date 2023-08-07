package upbrella.be.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.response.AllUsersInfoResponse;
import upbrella.be.user.dto.response.SingleUserInfoResponse;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.user.exception.NonExistingMemberException;
import upbrella.be.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호를 인자로 받아")
    class LoginTest {
        @Test
        @DisplayName("회원은 로그인할 수 있다.")
        void success() {
            // given
            User user = User.builder()
                    .id(1L)
                    .socialId(23132L)
                    .accountNumber("110-421-674103")
                    .bank("신한")
                    .name("홍길동")
                    .phoneNumber("010-2084-3478")
                    .adminStatus(false)
                    .build();

            given(userRepository.findBySocialId(23132L))
                    .willReturn(Optional.of(user));

            // when
            long loginedUserId = userService.login(23132L);

            // then
            assertAll(
                    () -> assertThat(loginedUserId).isEqualTo(1L),
                    () -> then(userRepository).should(times(1))
                            .findBySocialId(23132L));
        }

        @Test
        @DisplayName("미가입된 사용자는 로그인 시 예외가 발생된다.")
        void nonExistingUser() {
            // given
            given(userRepository.findBySocialId(23132L))
                    .willReturn(Optional.ofNullable(null));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.login(23132L))
                            .isInstanceOf(NonExistingMemberException.class),
                    () -> then(userRepository).should(times(1))
                            .findBySocialId(23132L));
        }
    }

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호와 이름, 전화 번호 등을 인자로 받아")
    class JoinTest {
        User user = User.builder()
                .id(1L)
                .socialId(23132L)
                .accountNumber("110-421-674103")
                .bank("신한")
                .name("홍길동")
                .phoneNumber("010-2084-3478")
                .adminStatus(false)
                .build();

        JoinRequest joinRequest = JoinRequest.builder()
                .name("홍길동")
                .bank("신한")
                .accountNumber("110-421-674103")
                .phoneNumber("010-2084-3478")
                .build();

        @Test
        @DisplayName("회원 가입할 수 있다.")
        void success() {

            // given
            given(userRepository.existsBySocialId(23132L))
                    .willReturn(false);
            given(userRepository.save(any(User.class)))
                    .willReturn(user);

            // when
            long joinedUserId = userService.join(23132L, joinRequest);

            // then
            assertAll(
                    () -> assertThat(joinedUserId).isEqualTo(1L),
                    () -> then(userRepository).should(times(1))
                            .save(any(User.class)),
                    () -> then(userRepository).should(times(1))
                            .existsBySocialId(23132L));
        }

        @Test
        @DisplayName("이미 회원 가입된 사용자는 예외가 발생된다.")
        void existingUser() {
            // given
            given(userRepository.existsBySocialId(23132L))
                    .willReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.join(23132L, joinRequest))
                            .isInstanceOf(ExistingMemberException.class),
                    () -> then(userRepository).should(times(1))
                            .existsBySocialId(23132L),
                    () -> then(userRepository).should(never())
                            .save(any(User.class)));
        }
    }

    @Nested
    @DisplayName("사용자는")
    class findUsersTest {

        // given
        User poro = User.builder()
                .name("포로")
                .phoneNumber("010-0000-0000")
                .bank("신한")
                .accountNumber("110-421")
                .adminStatus(true)
                .socialId(12345667L)
                .build();

        User luke = User.builder()
                .name("김성규")
                .phoneNumber("010-1223-3444")
                .bank("우리")
                .accountNumber("1002-473")
                .adminStatus(false)
                .socialId(3892710212132L)
                .build();

        @Test
        @DisplayName("회원 목록을 조회할 수 있다.")
        void success() {

            // given
            AllUsersInfoResponse expected = AllUsersInfoResponse.builder()
                    .users(List.of(
                            SingleUserInfoResponse.fromUser(poro),
                            SingleUserInfoResponse.fromUser(luke)
                    ))
                    .build();

            given(userRepository.findAll())
                    .willReturn(List.of(poro, luke));

            // when
            AllUsersInfoResponse allUsersInfoResponse = userService.findUsers();

            // then
            assertAll(
                    () -> assertThat(allUsersInfoResponse)
                            .usingRecursiveComparison()
                            .isEqualTo(expected),
                    () -> then(userRepository).should(times(1))
                            .findAll());
        }

        @Test
        @DisplayName("존재하는 회원이 없으면 빈 목록이 반환된다.")
        void nonExistingUser() {

            // given
            given(userRepository.findAll())
                    .willReturn(List.of());

            // when
            AllUsersInfoResponse allUsersInfoResponse = userService.findUsers();

            // then
            assertAll(
                    () -> assertThat(allUsersInfoResponse.getUsers().size()).isEqualTo(0),
                    () -> then(userRepository).should(times(1))
                            .findAll());
        }
    }
}