package upbrella.be.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import upbrella.be.config.FixtureBuilderFactory;
import upbrella.be.config.FixtureFactory;
import upbrella.be.rent.entity.History;
import upbrella.be.rent.service.RentService;
import upbrella.be.user.dto.request.JoinRequest;
import upbrella.be.user.dto.request.KakaoAccount;
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.*;
import upbrella.be.user.entity.BlackList;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.BlackListUserException;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.user.exception.NonExistingMemberException;
import upbrella.be.user.repository.BlackListRepository;
import upbrella.be.user.repository.UserRepository;
import upbrella.be.util.AesEncryptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlackListRepository blackListRepository;
    @Mock
    private RentService rentService;
    @Mock
    private AesEncryptor aesEncryptor;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호를 인자로 받아")
    class LoginTest {

        private User user;
        private long notExistingSocialId;

        @BeforeEach
        void setUp() {

            user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
            notExistingSocialId = FixtureBuilderFactory.buildInteger(10000000);
        }

        @Test
        @DisplayName("회원은 로그인할 수 있다.")
        void success() {

            // given
            given(userRepository.findBySocialId(user.getSocialId()))
                    .willReturn(Optional.of(user));

            // when
            SessionUser loginedUserId = userService.login(user.getSocialId());

            // then
            assertAll(
                    () -> assertThat(loginedUserId.getId()).isEqualTo(user.getId()),
                    () -> then(userRepository).should(times(1))
                            .findBySocialId(user.getSocialId()));
        }

        @Test
        @DisplayName("미가입된 사용자는 로그인 시 예외가 발생된다.")
        void nonExistingUser() {

            // given
            given(userRepository.findBySocialId(notExistingSocialId))
                    .willReturn(Optional.ofNullable(null));

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.login(notExistingSocialId))
                            .isInstanceOf(NonExistingMemberException.class),
                    () -> then(userRepository).should(times(1))
                            .findBySocialId(notExistingSocialId));
        }
    }

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호와 이름, 전화 번호 등을 인자로 받아")
    class JoinTest {
        private User user;
        private long notExistingSocialId;
        private long existingSocialId;
        private JoinRequest joinRequest;

        @BeforeEach
        void setUp() {

            user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
            existingSocialId = FixtureBuilderFactory.buildLong(100000000);
            notExistingSocialId = FixtureBuilderFactory.buildLong(100000000);
            joinRequest = FixtureFactory.buildJoinRequestWithUser(user);
        }

        @Test
        @DisplayName("회원 가입할 수 있다.")
        void success() {
            // given
            KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                    .id(notExistingSocialId)
                    .kakaoAccount(
                            KakaoAccount.builder()
                                    .email("email@email.com")
                                    .build())
                    .build();
            given(userRepository.existsBySocialId(notExistingSocialId))
                    .willReturn(false);
            given(userRepository.save(any(User.class)))
                    .willReturn(user);

            // when
            SessionUser joinedUserId = userService.join(kakaoUser, joinRequest);

            // then
            assertAll(
                    () -> assertThat(joinedUserId.getId()).isEqualTo(user.getId()),
                    () -> then(userRepository).should(times(1))
                            .save(any(User.class)),
                    () -> then(userRepository).should(times(1))
                            .existsBySocialId(notExistingSocialId));
        }

        @Test
        @DisplayName("이미 회원 가입된 사용자는 예외가 발생된다.")
        void existingUser() {
            // given
            KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                    .id(existingSocialId)
                    .kakaoAccount(
                            KakaoAccount.builder()
                                    .email("email@email.com")
                                    .build())
                    .build();

            given(userRepository.existsBySocialId(existingSocialId))
                    .willReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.join(kakaoUser, joinRequest))
                            .isInstanceOf(ExistingMemberException.class),
                    () -> then(userRepository).should(times(1))
                            .existsBySocialId(existingSocialId),
                    () -> then(userRepository).should(never())
                            .save(any(User.class)));
        }
    }

    @Nested
    @DisplayName("우산을 빌린 사용자는")
    class findBorrowedUmbrellaTest {

        private SessionUser sessionUser;
        private History history;

        @BeforeEach
        void setUp() {

            User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
            sessionUser = SessionUser.fromUser(user);
            history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample();
        }

        @Test
        @DisplayName("자신이 빌린 우산 대여 내역을 조회할 수 있다.")
        void success() {

            // given
            given(rentService.findRentalHistoryByUser(sessionUser))
                    .willReturn(history);

            // when
            UmbrellaBorrowedByUserResponse umbrellaBorrowedByUser = userService.findUmbrellaBorrowedByUser(sessionUser);

            // then
            assertAll(
                    () -> assertThat(umbrellaBorrowedByUser.getUuid())
                            .isEqualTo(history.getUmbrella().getUuid()),
                    () -> then(rentService).should(times(1))
                            .findRentalHistoryByUser(sessionUser));
        }
    }

    @Nested
    @DisplayName("사용자는")
    class findUsersTest {

        List<User> users = new ArrayList<>();
        List<User> expectedUsers = new ArrayList<>();

        @BeforeEach
        void setUp() {

            for (int i = 0; i < 1; i++) {
                User sample = User.builder()
                        .id(1L)
                        .socialId(1L)
                        .name("사용자")
                        .phoneNumber("010-1234-5678")
                        .bank(aesEncryptor.encrypt("농협"))
                        .accountNumber(aesEncryptor.encrypt("123-456-789"))
                        .build();

                User expectedSample = User.builder()
                        .id(1L)
                        .socialId(1L)
                        .name("사용자")
                        .phoneNumber("010-1234-5678")
                        .bank(aesEncryptor.encrypt("농협"))
                        .accountNumber(aesEncryptor.encrypt("123-456-789"))
                        .build();

                users.add(sample);
                expectedUsers.add(expectedSample);
            }
        }

        @Test
        @DisplayName("회원 목록을 조회할 수 있다.")
        void success() {

            // given
            AllUsersInfoResponse expected = AllUsersInfoResponse.builder()
                    .users(
                            expectedUsers.stream()
                                    .map(user -> user.decryptData(aesEncryptor))
                                    .map(SingleUserInfoResponse::fromUser)
                                    .collect(Collectors.toList())
                    )
                    .build();

            given(userRepository.findAll())
                    .willReturn(users);

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

    @Test
    @DisplayName("사용자는 자신의 은행 정보를 수정할 수 있다.")
    void updateBankTest() {
        // given
        User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
        UpdateBankAccountRequest updateBankInfoRequest = FixtureBuilderFactory.builderBankAccount().sample();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        userService.updateUserBankAccount(user.getId(), updateBankInfoRequest);

        // then
        assertAll(
                () -> then(userRepository).should(times(1))
                        .findById(user.getId()),
                () -> assertThat(user.getBank()).isEqualTo(aesEncryptor.encrypt(updateBankInfoRequest.getBank())),
                () -> assertThat(user.getAccountNumber()).isEqualTo(aesEncryptor.encrypt(updateBankInfoRequest.getAccountNumber())));
    }

    @Test
    @DisplayName("사용자는 자신이 회원탈퇴를 하면 정보가 임의의 값으로 변경되고 탈퇴된다.")
    void deleteUser() {
        // given
        User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        // when

        userService.deleteUser(user.getId());

        // then
        assertAll(
                () -> assertThat(user.getSocialId()).isEqualTo(0L),
                () -> assertThat(user.getName()).isEqualTo("탈퇴한 회원"),
                () -> assertThat(user.getPhoneNumber()).isEqualTo("deleted"),
                () -> assertThat(user.isAdminStatus()).isEqualTo(false),
                () -> assertThat(user.getBank()).isEqualTo(null),
                () -> assertThat(user.getAccountNumber()).isEqualTo(null)
        );
    }

    @Nested
    @DisplayName("관리자는")
    class withdrawTest {
        @Test
        @DisplayName("사용자를 강제 탈퇴시키고 블랙리스트에 등록할 수 있다.")
        void withdrawTest() {
            // given
            User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

            // when
            userService.withdrawUser(user.getId());

            // then
            assertAll(
                    () -> then(userRepository).should(times(1))
                            .findById(user.getId()),
                    () -> assertThat(user.getSocialId()).isEqualTo(0L),
                    () -> assertThat(user.getName()).isEqualTo("정지된 회원"),
                    () -> assertThat(user.getPhoneNumber()).isEqualTo("deleted"),
                    () -> assertThat(user.isAdminStatus()).isEqualTo(false),
                    () -> assertThat(user.getBank()).isEqualTo(null),
                    () -> assertThat(user.getAccountNumber()).isEqualTo(null),
                    () -> then(blackListRepository).should(times(1))
                            .save(any(BlackList.class))
            );
        }

        @Test
        @DisplayName("이미 탈퇴했거나 블랙리스트에 등록한 회원일 경우 예외가 발생한다.")
        void blockedUserTest() {
            // given
            User blockedUser = FixtureBuilderFactory.builderUser(aesEncryptor).set("id", 0L).sample();
            given(userRepository.findById(blockedUser.getId())).willReturn(Optional.of(blockedUser));

            // when
            userService.withdrawUser(blockedUser.getId());

            // then
            assertAll(
                    () -> then(userRepository).should(times(1))
                            .findById(blockedUser.getId()),
                    () -> assertThatThrownBy(() -> userService.withdrawUser(blockedUser.getId()))
                            .isInstanceOf(NonExistingMemberException.class)
            );
        }
    }

    @Test
    @DisplayName("블랙리스트에 들어간 회원은 회원가입이 불가능하다.")
    void blackListMemberJoinTest() {
        // given

        KakaoLoginResponse kakaoUser = KakaoLoginResponse.builder()
                .id(0L)
                .kakaoAccount(
                        KakaoAccount.builder()
                                .email("email@email.com")
                                .build())
                .build();

        long blackList = 0L;
        given(blackListRepository.existsBySocialId(blackList)).willReturn(true);
        given(userRepository.existsBySocialId(blackList)).willReturn(false);
        JoinRequest joinRequest = FixtureBuilderFactory.builderJoinRequest().sample();

        // when


        // then
        assertThatThrownBy(() -> userService.join(kakaoUser, joinRequest))
                .isInstanceOf(BlackListUserException.class);
    }

    @Test
    @DisplayName("사용자는 계좌 정보를 삭제할 수 있다.")
    void deleteUserBankAccountTest() {
        // given
        User user = FixtureBuilderFactory.builderUser(aesEncryptor).sample();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        userService.deleteUser(user.getId());

        // then
        assertAll(
                () -> assertThat(user.getBank()).isNull(),
                () -> assertThat(user.getAccountNumber()).isNull()
        );
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    void blackListTest() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        given(blackListRepository.findAll()).willReturn(List.of(BlackList.builder()
                .id(1L)
                .socialId(1L)
                .blockedAt(now)
                .build()));

        // then
        assertAll(
                () -> assertThat(userService.findBlackList().getBlackList().size()).isEqualTo(1),
                () -> assertThat(userService.findBlackList().getBlackList().get(0).getBlockedAt()).isEqualTo(now)
        );
    }

    @Test
    @DisplayName("사용자는 블랙리스트의 유저를 삭제할 수 있다.")
    void deleteBlackListTest() {
        // given
        long blackListId = 1L;
        doNothing().when(blackListRepository).deleteById(blackListId);

        // when
        userService.deleteBlackList(blackListId);

        // then
        verify(blackListRepository, times(1)).deleteById(blackListId);
    }

    @Test
    @DisplayName("사용자는 관리자 권한을 변경할 수 있다.")
    void updateAdminStatusTest() {
        // given
        User user = FixtureBuilderFactory.builderUser(aesEncryptor)
                .set("adminStatus", false)
                .sample();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.updateAdminStatus(1L);

        // then
        assertThat(user.isAdminStatus()).isTrue();
    }

    @Test
    @DisplayName("정상적으로 로그인하지 않은 경우 개인정보 조회를 할 수 없다.")
    void notLoginException() {
        // given
        SessionUser user = SessionUser.builder().build();

        // when

        // then
        assertThatThrownBy(() -> userService.findDecryptedUserById(user))
                .isInstanceOf(NonExistingMemberException.class);
    }
}
