package upbrella.be.user.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import upbrella.be.config.FixtureBuilderFactory
import upbrella.be.config.FixtureFactory
import upbrella.be.rent.entity.History
import upbrella.be.rent.service.RentService
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.request.UpdateBankAccountRequest
import upbrella.be.user.dto.response.AllUsersInfoResponse
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.dto.response.SingleUserInfoResponse
import upbrella.be.user.entity.BlackList
import upbrella.be.user.entity.User
import upbrella.be.user.exception.BlackListUserException
import upbrella.be.user.exception.ExistingMemberException
import upbrella.be.user.exception.NonExistingMemberException
import upbrella.be.user.repository.*
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var blackListReader: BlackListReader

    @Mock
    private lateinit var blackListWriter: BlackListWriter

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userReader: UserReader

    @Mock
    private lateinit var userWriter: UserWriter

    @Mock
    private lateinit var rentService: RentService
    @Mock
    private lateinit var aesEncryptor: AesEncryptor
    @InjectMocks
    private lateinit var userService: UserService

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호를 인자로 받아")
    inner class LoginTest {

        private lateinit var user: User
        private var notExistingSocialId: Long = 0

        @BeforeEach
        fun setUp() {
            user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
            notExistingSocialId = FixtureBuilderFactory.buildInteger(10000000).toLong()
        }

        @Test
        @DisplayName("회원은 로그인할 수 있다.")
        fun success() {
            // given
            given(userReader.findBySocialId(user.socialId)).willReturn(user)

            // when
            val loginedUserId = userService.login(user.socialId)

            // then
            assertAll(
                { assertThat(loginedUserId.id).isEqualTo(user.id) },
                {
                    then(userReader).should(times(1))
                        .findBySocialId(user.socialId)
                }
            )
        }

        @Test
        @DisplayName("미가입된 사용자는 로그인 시 예외가 발생된다.")
        fun nonExistingUser() {
            // given
            given(userReader.findBySocialId(notExistingSocialId))
                .willThrow(NonExistingMemberException("회원이 존재하지 않습니다."))

            // when & then
            assertAll(
                {
                    assertThatThrownBy { userService.login(notExistingSocialId) }
                        .isInstanceOf(NonExistingMemberException::class.java)
                },
                {
                    then(userReader).should(times(1))
                        .findBySocialId(notExistingSocialId)
                }
            )
        }
    }

    @Nested
    @DisplayName("사용자의 카카오 소셜 고유 번호와 이름, 전화 번호 등을 인자로 받아")
    inner class JoinTest {

        private lateinit var user: User
        private var notExistingSocialId: Long = 0
        private var existingSocialId: Long = 0
        private lateinit var joinRequest: JoinRequest

        @BeforeEach
        fun setUp() {
            user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
            existingSocialId = FixtureBuilderFactory.buildLong(100000000)
            notExistingSocialId = FixtureBuilderFactory.buildLong(100000000)
            joinRequest = FixtureFactory.buildJoinRequestWithUser(user)
        }

        @Test
        @DisplayName("회원 가입할 수 있다.")
        fun success() {
            // given
            val kakaoUser = KakaoLoginResponse(
                id = notExistingSocialId,
                kakaoAccount = KakaoAccount(
                    email = "email@email.com",
                )
            )

            given(userReader.existsBySocialId(notExistingSocialId))
                .willReturn(false)
            given(userWriter.save(any<User>() ?: user)).willReturn(user)

            // when
            val joinedUserId = userService.join(kakaoUser, joinRequest)

            // then
            assertAll(
                { assertThat(joinedUserId.id).isEqualTo(user.id) },
                {
                    then(userWriter).should(times(1)).save(any<User>() ?: user)
                },
                {
                    then(userReader).should(times(1))
                        .existsBySocialId(notExistingSocialId)
                }
            )
        }

        @Test
        @DisplayName("이미 회원 가입된 사용자는 예외가 발생된다.")
        fun existingUser() {
            // given
            val kakaoUser = KakaoLoginResponse(
                id = existingSocialId,
                kakaoAccount = KakaoAccount(
                    email = "email@email.com",
                )
            )

            given(userReader.existsBySocialId(existingSocialId))
                .willReturn(true)

            // when & then
            assertAll(
                {
                    assertThatThrownBy { userService.join(kakaoUser, joinRequest) }
                        .isInstanceOf(ExistingMemberException::class.java)
                },
                {
                    then(userReader).should(times(1))
                        .existsBySocialId(existingSocialId)
                },
                {
                    then(userWriter).should(never())
                        .save(any<User>() ?: user)
                }
            )
        }
    }

    @Nested
    @DisplayName("우산을 빌린 사용자는")
    inner class FindBorrowedJavaUmbrellaTest {

        private lateinit var sessionUser: SessionUser
        private lateinit var history: History

        @BeforeEach
        fun setUp() {
            val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
            sessionUser = SessionUser.fromUser(user)
            history = FixtureBuilderFactory.builderHistory(aesEncryptor).sample()
        }

        @Test
        @DisplayName("자신이 빌린 우산 대여 내역을 조회할 수 있다.")
        fun success() {
            // given
            given(rentService.findRentalHistoryByUser(sessionUser))
                .willReturn(history)

            // when
            val umbrellaBorrowedByUser = userService.findUmbrellaBorrowedByUser(sessionUser)

            // then
            assertAll(
                {
                    assertThat(umbrellaBorrowedByUser.uuid)
                        .isEqualTo(history.umbrella.uuid)
                },
                {
                    then(rentService).should(times(1))
                        .findRentalHistoryByUser(sessionUser)
                }
            )
        }
    }

    @Test
    @DisplayName("사용자는 자신의 은행 정보를 수정할 수 있다.")
    fun updateBankTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
        val updateBankInfoRequest: UpdateBankAccountRequest =
            FixtureBuilderFactory.builderBankAccount().sample()
        given(userReader.findUserById(user.id!!)).willReturn(user)

        // when
        userService.updateUserBankAccount(user.id!!, updateBankInfoRequest)

        // then
        assertAll(
            {
                then(userReader).should(times(1)).findUserById(user.id!!)
            },
            {
                assertThat(user.bank)
                    .isEqualTo(aesEncryptor.encrypt(updateBankInfoRequest.bank))
            },
            {
                assertThat(user.accountNumber)
                    .isEqualTo(aesEncryptor.encrypt(updateBankInfoRequest.accountNumber))
            }
        )
    }

    @Test
    @DisplayName("사용자는 자신이 회원탈퇴를 하면 정보가 임의의 값으로 변경되고 탈퇴된다.")
    fun deleteUser() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
        given(userReader.findUserById(user.id!!)).willReturn(user)

        // when
        userService.deleteUser(user.id!!)

        // then
        assertAll(
            { assertThat(user.socialId).isEqualTo(-(user.id!!)) },
            { assertThat(user.name).isEqualTo("탈퇴한 회원") },
            { assertThat(user.phoneNumber).isEqualTo("deleted") },
            { assertThat(user.email).isEqualTo("deleted") },
            { assertThat(user.adminStatus).isFalse() },
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }

    @Nested
    @DisplayName("관리자는")
    inner class WithdrawTest {

        @Test
        @DisplayName("사용자를 강제 탈퇴시키고 블랙리스트에 등록할 수 있다.")
        fun withdrawTest() {
            // given
            val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
            val blackList = BlackList(1L, LocalDateTime.now(), 1L)
            given(userReader.findUserById(user.id!!)).willReturn(user)

            // when
            userService.withdrawUser(user.id!!)

            // then
            assertAll(
                {
                    then(userReader).should(times(1))
                        .findUserById(user.id!!)
                },
                { assertThat(user.socialId).isEqualTo(-(user.id!!)) },
                { assertThat(user.name).isEqualTo("정지된 회원") },
                { assertThat(user.phoneNumber).isEqualTo("deleted") },
                { assertThat(user.email).isEqualTo("deleted") },
                { assertThat(user.adminStatus).isFalse() },
                { assertThat(user.bank).isNull() },
                { assertThat(user.accountNumber).isNull() },
                {
                    then(blackListWriter).should(times(1))
                        .save(any<BlackList>() ?: blackList)
                }
            )
        }

        @Test
        @DisplayName("이미 탈퇴했거나 블랙리스트에 등록한 회원일 경우 예외가 발생한다.")
        fun blockedUserTest() {
            // given
            val blockedUser = FixtureBuilderFactory.builderUser(aesEncryptor)
                .set("id", 0L)
                .sample()

            given(userReader.findUserById(blockedUser.id!!)).willReturn(blockedUser)

            // when
            userService.withdrawUser(blockedUser.id!!)

            // then
            assertAll(
                {
                    then(userReader).should(times(1))
                        .findUserById(blockedUser.id!!)
                },
                {
                    assertThatThrownBy { userService.withdrawUser(blockedUser.id!!) }
                        .isInstanceOf(NonExistingMemberException::class.java)
                }
            )
        }
    }

    @Test
    @DisplayName("블랙리스트에 들어간 회원은 회원가입이 불가능하다.")
    fun blackListMemberJoinTest() {
        // given
        val kakaoUser = KakaoLoginResponse(
            id = 0L,
            kakaoAccount = KakaoAccount(
                email = "email@email.com",
            )
        )

        val blackListId = 0L
        given(blackListReader.existsBySocialId(blackListId)).willReturn(true)
        given(userReader.existsBySocialId(blackListId)).willReturn(false)
        val joinRequest = FixtureBuilderFactory.builderJoinRequest().sample()

        // when & then
        assertThatThrownBy { userService.join(kakaoUser, joinRequest) }
            .isInstanceOf(BlackListUserException::class.java)
    }

    @Test
    @DisplayName("사용자는 계좌 정보를 삭제할 수 있다.")
    fun deleteUserBankAccountTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
        given(userReader.findUserById(user.id!!)).willReturn(user)

        // when
        userService.deleteUser(user.id!!)

        // then
        assertAll(
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }


    @Test
    @DisplayName("사용자는 관리자 권한을 변경할 수 있다.")
    fun updateAdminStatusTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor)
            .set("adminStatus", false)
            .sample()

        given(userReader.findUserById(1L)).willReturn(user)

        // when
        userService.updateAdminStatus(1L)

        // then
        assertThat(user.adminStatus).isTrue()
    }

    @Test
    @DisplayName("정상적으로 로그인하지 않은 경우 개인정보 조회를 할 수 없다.")
    fun notLoginException() {
        // given
        val user = SessionUser(
            id = 1L,
            adminStatus = false
        )

        // when & then
        assertThatThrownBy { userService.findDecryptedUserById(user) }
            .isInstanceOf(NonExistingMemberException::class.java)
    }

}
