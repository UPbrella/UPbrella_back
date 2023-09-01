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
import upbrella.be.user.dto.request.UpdateBankAccountRequest;
import upbrella.be.user.dto.response.AllUsersInfoResponse;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.dto.response.SingleUserInfoResponse;
import upbrella.be.user.dto.response.UmbrellaBorrowedByUserResponse;
import upbrella.be.user.entity.BlackList;
import upbrella.be.user.entity.User;
import upbrella.be.user.exception.BlackListUserException;
import upbrella.be.user.exception.ExistingMemberException;
import upbrella.be.user.exception.NonExistingMemberException;
import upbrella.be.user.repository.BlackListRepository;
import upbrella.be.user.repository.UserRepository;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlackListRepository blackListRepository;
    @Mock
    private RentService rentService;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호를 인자로 받아")
    class LoginTest {

        private User user;
        private long notExistingSocialId;

        @BeforeEach
        void setUp() {

            user = FixtureBuilderFactory.builderUser().sample();
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

            user = FixtureBuilderFactory.builderUser().sample();
            existingSocialId = FixtureBuilderFactory.buildLong(100000000);
            notExistingSocialId = FixtureBuilderFactory.buildLong(100000000);
            joinRequest = FixtureFactory.buildJoinRequestWithUser(user);
        }

        @Test
        @DisplayName("회원 가입할 수 있다.")
        void success() {

            // given
            given(userRepository.existsBySocialId(notExistingSocialId))
                    .willReturn(false);
            given(userRepository.save(any(User.class)))
                    .willReturn(user);

            // when
            SessionUser joinedUserId = userService.join(notExistingSocialId, joinRequest);

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
            given(userRepository.existsBySocialId(existingSocialId))
                    .willReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.join(existingSocialId, joinRequest))
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

            User user = FixtureBuilderFactory.builderUser().sample();
            sessionUser = SessionUser.fromUser(user);
            history = FixtureBuilderFactory.builderHistory().sample();
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

        @BeforeEach
        void setUp() {

            for (int i = 0; i < 5; i++) {
                users.add(FixtureBuilderFactory.builderUser().sample());
            }
        }

        @Test
        @DisplayName("회원 목록을 조회할 수 있다.")
        void success() {

            // given
            AllUsersInfoResponse expected = AllUsersInfoResponse.builder()
                    .users(
                            users.stream()
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
        User user = FixtureBuilderFactory.builderUser().sample();
        UpdateBankAccountRequest updateBankInfoRequest = FixtureBuilderFactory.builderBankAccount().sample();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        userService.updateUserBankAccount(user.getId(), updateBankInfoRequest);

        // then
        assertAll(
                () -> then(userRepository).should(times(1))
                        .findById(user.getId()),
                () -> assertThat(user.getBank()).isEqualTo(updateBankInfoRequest.getBank()),
                () -> assertThat(user.getAccountNumber()).isEqualTo(updateBankInfoRequest.getAccountNumber()));
    }

    @Test
    @DisplayName("사용자는 자신이 회원탈퇴를 하면 정보가 임의의 값으로 변경되고 탈퇴된다.")
    void deleteUser() {
        // given
        User user = FixtureBuilderFactory.builderUser().sample();
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

    @Test
    @DisplayName("관리자가 회원탈퇴 시키면 블랙리스트에 들어가고 회원정보는 초기화된다.")
    void withdrawTest() {
        // given
        User user = FixtureBuilderFactory.builderUser().sample();
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
    @DisplayName("블랙리스트에 들어간 회원은 회원가입이 불가능하다.")
    void blackListMemberJoinTest() {
        // given
        long blackList = 0L;
        given(blackListRepository.existsBySocialId(blackList)).willReturn(true);
        given(userRepository.existsBySocialId(blackList)).willReturn(false);
        JoinRequest joinRequest = FixtureBuilderFactory.builderJoinRequest().sample();

        // when


        // then
        assertThatThrownBy(() -> userService.join(blackList, joinRequest))
                .isInstanceOf(BlackListUserException.class);
    }
}
