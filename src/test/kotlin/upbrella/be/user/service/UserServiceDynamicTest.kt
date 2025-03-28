package upbrella.be.user.service

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import upbrella.be.user.dto.request.JoinRequest
import upbrella.be.user.dto.request.KakaoAccount
import upbrella.be.user.dto.response.KakaoLoginResponse
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.entity.User
import upbrella.be.user.exception.ExistingMemberException
import upbrella.be.user.exception.NonExistingMemberException
import upbrella.be.user.repository.UserRepository
import upbrella.be.util.AesEncryptor

@SpringBootTest
class UserServiceDynamicTest {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var aesEncryptor: AesEncryptor

    @Transactional
    @TestFactory
    @DisplayName("사용자는 회원 가입과 로그인을 할 수 있다.")
    fun joinTest(): Collection<DynamicTest> {
        // given
        val user = User(
            23132L,
            "홍길동",
            "010-2084-3478",
            "email@email.com",
            false,
            aesEncryptor.encrypt("신한"),
            aesEncryptor.encrypt("110-421-674103"),
            1L
        )

        val joinRequest = JoinRequest.builder()
            .name("홍길동")
            .bank("신한")
            .accountNumber("110-421-674103")
            .phoneNumber("010-2084-3478")
            .build()

        val kakaoUser = KakaoLoginResponse.builder()
            .id(23132L)
            .kakaoAccount(
                KakaoAccount.builder()
                    .email("email@email.com")
                    .build()
            )
            .build()

        return listOf(
            DynamicTest.dynamicTest("새로 가입한 유저는 DB에 저장된다.") {
                val joined: SessionUser = userService.join(kakaoUser, joinRequest)
                val foundUser = userRepository.findById(joined.id)

                assertAll(
                    { assertTrue(foundUser.isPresent) },
                    { assertEquals(user.name, foundUser.get().name) },
                    { assertEquals(user.phoneNumber, foundUser.get().phoneNumber) },
                    { assertEquals(user.adminStatus, foundUser.get().adminStatus) },
                    { assertEquals(user.socialId, foundUser.get().socialId) },
                    { assertEquals(aesEncryptor.decrypt(user.accountNumber), aesEncryptor.decrypt(foundUser.get().accountNumber)) },
                    { assertEquals(aesEncryptor.decrypt(user.bank), aesEncryptor.decrypt(foundUser.get().bank)) }
                )
            },
            DynamicTest.dynamicTest("이미 가입된 유저는 예외가 발생된다.") {
                assertThatThrownBy {
                    userService.join(kakaoUser, joinRequest)
                }.isInstanceOf(ExistingMemberException::class.java)
            },
            DynamicTest.dynamicTest("존재하지 않는 아이디로 로그인하면 예외가 발생한다.") {
                assertThatThrownBy {
                    userService.login(32322L)
                }.isInstanceOf(NonExistingMemberException::class.java)
            },
            DynamicTest.dynamicTest("회원 가입한 아이디로 로그인할 수 있다.") {
                val logined: SessionUser = userService.login(user.socialId)
                val foundUser = userRepository.findById(logined.id)

                assertAll(
                    { assertTrue(foundUser.isPresent) },
                    { assertEquals(user.name, foundUser.get().name) },
                    { assertEquals(user.phoneNumber, foundUser.get().phoneNumber) },
                    { assertEquals(user.adminStatus, foundUser.get().adminStatus) },
                    { assertEquals(user.socialId, foundUser.get().socialId) },
                    { assertEquals(aesEncryptor.decrypt(user.accountNumber), aesEncryptor.decrypt(foundUser.get().accountNumber)) },
                    { assertEquals(aesEncryptor.decrypt(user.bank), aesEncryptor.decrypt(foundUser.get().bank)) }
                )
            }
        )
    }
}
