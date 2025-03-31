package upbrella.be.user.service

import org.assertj.core.api.Assertions.*
import org.assertj.core.api.AssertionsForClassTypes.assertThatCode
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
import upbrella.be.user.dto.response.*
import upbrella.be.user.entity.BlackList
import upbrella.be.user.entity.User
import upbrella.be.user.exception.BlackListUserException
import upbrella.be.user.exception.ExistingMemberException
import upbrella.be.user.exception.NonExistingMemberException
import upbrella.be.user.repository.BlackListRepository
import upbrella.be.user.repository.UserRepository
import upbrella.be.util.AesEncryptor
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var blackListRepository: BlackListRepository

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
            given(userRepository.findBySocialId(user.socialId)).willReturn(Optional.of(user))

            // when
            val loginedUserId = userService.login(user.socialId)

            // then
            assertAll(
                { assertThat(loginedUserId.id).isEqualTo(user.id) },
                {
                    then(userRepository).should(times(1))
                        .findBySocialId(user.socialId)
                }
            )
        }

        @Test
        @DisplayName("미가입된 사용자는 로그인 시 예외가 발생된다.")
        fun nonExistingUser() {
            // given
            given(userRepository.findBySocialId(notExistingSocialId))
                .willReturn(Optional.ofNullable(null))

            // when & then
            assertAll(
                {
                    assertThatThrownBy { userService.login(notExistingSocialId) }
                        .isInstanceOf(NonExistingMemberException::class.java)
                },
                {
                    then(userRepository).should(times(1))
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
            val kakaoUser = KakaoLoginResponse.builder()
                .id(notExistingSocialId)
                .kakaoAccount(
                    KakaoAccount.builder()
                        .email("email@email.com")
                        .build()
                )
                .build()

            given(userRepository.existsBySocialId(notExistingSocialId))
                .willReturn(false)
            given(userRepository.save(any<User>())).willReturn(user)

            // when
            val joinedUserId = userService.join(kakaoUser, joinRequest)

            // then
            assertAll(
                { assertThat(joinedUserId.id).isEqualTo(user.id) },
                {
                    then(userRepository).should(times(1)).save(any<User>())
                },
                {
                    then(userRepository).should(times(1))
                        .existsBySocialId(notExistingSocialId)
                }
            )
        }

        @Test
        @DisplayName("이미 회원 가입된 사용자는 예외가 발생된다.")
        fun existingUser() {
            // given
            val kakaoUser = KakaoLoginResponse.builder()
                .id(existingSocialId)
                .kakaoAccount(
                    KakaoAccount.builder()
                        .email("email@email.com")
                        .build()
                )
                .build()

            given(userRepository.existsBySocialId(existingSocialId))
                .willReturn(true)

            // when & then
            assertAll(
                {
                    assertThatThrownBy { userService.join(kakaoUser, joinRequest) }
                        .isInstanceOf(ExistingMemberException::class.java)
                },
                {
                    then(userRepository).should(times(1))
                        .existsBySocialId(existingSocialId)
                },
                {
                    then(userRepository).should(never())
                        .save(any<User>())
                }
            )
        }
    }

    @Nested
    @DisplayName("우산을 빌린 사용자는")
    inner class FindBorrowedUmbrellaTest {

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

    @Nested
    @DisplayName("사용자는")
    inner class FindUsersTest {

        private val users: MutableList<User> = ArrayList()
        private val expectedUsers: MutableList<User> = ArrayList()

        @BeforeEach
        fun setUp() {
            for (i in 0 until 1) {
                val sample = User(
                    1L,
                    "사용자",
                    "010-1234-5678",
                    "",
                    false,
                    aesEncryptor.encrypt("농협"),
                    aesEncryptor.encrypt("123-456-789"),
                    1L
                )
                val expectedSample = User(
                    1L,
                    "사용자",
                    "010-1234-5678",
                    "",
                    false,
                    aesEncryptor.encrypt("농협"),
                    aesEncryptor.encrypt("123-456-789"),
                    1L
                )

                users.add(sample)
                expectedUsers.add(expectedSample)
            }
        }

        @Test
        @DisplayName("회원 목록을 조회할 수 있다.")
        fun success() {
            // given
            val expected = AllUsersInfoResponse.builder()
                .users(
                    expectedUsers.stream()
                        .map { user -> user.decryptData(aesEncryptor) }
                        .map { decrypted -> SingleUserInfoResponse.fromUser(decrypted) }
                        .collect(Collectors.toList())
                )
                .build()

            given(userRepository.findAll()).willReturn(users)

            // when
            val allUsersInfoResponse = userService.findUsers()

            // then
            assertAll(
                {
                    assertThat(allUsersInfoResponse)
                        .usingRecursiveComparison()
                        .isEqualTo(expected)
                },
                {
                    then(userRepository).should(times(1)).findAll()
                }
            )
        }

        @Test
        @DisplayName("존재하는 회원이 없으면 빈 목록이 반환된다.")
        fun nonExistingUser() {
            // given
            given(userRepository.findAll())
                .willReturn(listOf())

            // when
            val allUsersInfoResponse = userService.findUsers()

            // then
            assertAll(
                {
                    assertThat(allUsersInfoResponse.users.size).isEqualTo(0)
                },
                {
                    then(userRepository).should(times(1)).findAll()
                }
            )
        }
    }

    @Test
    @DisplayName("사용자는 자신의 은행 정보를 수정할 수 있다.")
    fun updateBankTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor).sample()
        val updateBankInfoRequest: UpdateBankAccountRequest = FixtureBuilderFactory.builderBankAccount().sample()
        given(userRepository.findById(user.id!!)).willReturn(Optional.of(user))

        // when
        userService.updateUserBankAccount(user.id, updateBankInfoRequest)

        // then
        assertAll(
            {
                then(userRepository).should(times(1)).findById(user.id!!)
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
        given(userRepository.findById(user.id!!)).willReturn(Optional.of(user))

        // when
        userService.deleteUser(user.id)

        // then
        assertAll(
            { assertThat(user.socialId).isEqualTo(0L) },
            { assertThat(user.name).isEqualTo("탈퇴한 회원") },
            { assertThat(user.phoneNumber).isEqualTo("deleted") },
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
            given(userRepository.findById(user.id!!)).willReturn(Optional.of(user))

            // when
            userService.withdrawUser(user.id)

            // then
            assertAll(
                {
                    then(userRepository).should(times(1))
                        .findById(user.id!!)
                },
                { assertThat(user.socialId).isEqualTo(0L) },
                { assertThat(user.name).isEqualTo("정지된 회원") },
                { assertThat(user.phoneNumber).isEqualTo("deleted") },
                { assertThat(user.adminStatus).isFalse() },
                { assertThat(user.bank).isNull() },
                { assertThat(user.accountNumber).isNull() },
                {
                    then(blackListRepository).should(times(1))
                        .save(any<BlackList>())
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

            given(userRepository.findById(blockedUser.id!!)).willReturn(Optional.of(blockedUser))

            // when
            userService.withdrawUser(blockedUser.id)

            // then
            assertAll(
                {
                    then(userRepository).should(times(1))
                        .findById(blockedUser.id!!)
                },
                {
                    assertThatThrownBy { userService.withdrawUser(blockedUser.id) }
                        .isInstanceOf(NonExistingMemberException::class.java)
                }
            )
        }
    }

    @Test
    @DisplayName("블랙리스트에 들어간 회원은 회원가입이 불가능하다.")
    fun blackListMemberJoinTest() {
        // given
        val kakaoUser = KakaoLoginResponse.builder()
            .id(0L)
            .kakaoAccount(
                KakaoAccount.builder()
                    .email("email@email.com")
                    .build()
            )
            .build()

        val blackListId = 0L
        given(blackListRepository.existsBySocialId(blackListId)).willReturn(true)
        given(userRepository.existsBySocialId(blackListId)).willReturn(false)
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
        given(userRepository.findById(user.id!!)).willReturn(Optional.of(user))

        // when
        userService.deleteUser(user.id)

        // then
        assertAll(
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }

    @Test
    @DisplayName("사용자는 블랙리스트를 조회할 수 있다.")
    fun blackListTest() {
        // given
        val now = LocalDateTime.now()

        given(blackListRepository.findAll()).willReturn(
            listOf(
                BlackList(1L, now, 1L)
            )
        )

        // when & then
        assertAll(
            { assertThat(userService.findBlackList().blackList.size).isEqualTo(1) },
            { assertThat(userService.findBlackList().blackList[0].blockedAt).isEqualTo(now) }
        )
    }

    @Test
    @DisplayName("사용자는 블랙리스트의 유저를 삭제할 수 있다.")
    fun deleteBlackListTest() {
        // given
        val blackListId = 1L
        doNothing().`when`(blackListRepository).deleteById(blackListId)

        // when
        userService.deleteBlackList(blackListId)

        // then
        verify(blackListRepository, times(1)).deleteById(blackListId)
    }

    @Test
    @DisplayName("사용자는 관리자 권한을 변경할 수 있다.")
    fun updateAdminStatusTest() {
        // given
        val user = FixtureBuilderFactory.builderUser(aesEncryptor)
            .set("adminStatus", false)
            .sample()

        given(userRepository.findById(1L)).willReturn(Optional.of(user))

        // when
        userService.updateAdminStatus(1L)

        // then
        assertThat(user.adminStatus).isTrue()
    }

    @Test
    @DisplayName("정상적으로 로그인하지 않은 경우 개인정보 조회를 할 수 없다.")
    fun notLoginException() {
        // given
        val user = SessionUser.builder().build()

        // when & then
        assertThatThrownBy { userService.findDecryptedUserById(user) }
            .isInstanceOf(NonExistingMemberException::class.java)
    }

    @Test
    @DisplayName("사용자가 블랙리스트에 등록되어 있으면 예외가 발생한다.")
    fun checkBlackListThrowTest() {
        // given
        val blackList = BlackList.createNewBlackList(1L)

        given(blackListRepository.findById(1L))
            .willReturn(Optional.of(blackList))

        // when & then
        assertThatThrownBy { userService.checkBlackList(1L) }
            .isInstanceOf(BlackListUserException::class.java)
    }

    @Test
    @DisplayName("사용자가 블랙리스트에 없으면 예외가 발생하지 않는다.")
    fun checkBlackListNotThrowTest() {
        // given
        given(blackListRepository.findById(1L))
            .willReturn(Optional.empty())

        // when & then
        assertThatCode {
            userService.checkBlackList(1L)
        }.doesNotThrowAnyException()
    }
}
