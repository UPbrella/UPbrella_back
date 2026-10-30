package upbrella.be.user.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserRepository
import upbrella.be.util.AesEncryptor
import javax.persistence.EntityManager

@SpringBootTest
@Transactional
class UserReadPersistenceTest {
    @Autowired private lateinit var users: UserRepository
    @Autowired private lateinit var service: UserService
    @Autowired private lateinit var cipher: AesEncryptor
    @Autowired private lateinit var entityManager: EntityManager

    private fun savedUser(): User = users.saveAndFlush(User(
        socialId = -61011111L, name = "조회 검증 사용자", email = "read-test@example.com",
        bank = cipher.encrypt("테스트은행"), accountNumber = cipher.encrypt("000000000000")
    ))

    private fun assertCiphertextRetained(id: Long, bank: String?, account: String?) {
        entityManager.flush()
        entityManager.clear()
        val stored = users.findById(id).get()
        assertThat(stored.bank).isEqualTo(bank)
        assertThat(stored.accountNumber).isEqualTo(account)
    }

    @Test
    fun `administrator member lookup does not persist decrypted fields on flush`() {
        val user = savedUser()
        val bank = user.bank
        val account = user.accountNumber
        val response = service.findUsers().users.single { it.id == user.id }
        assertThat(response.bank).isEqualTo("테스트은행")
        assertThat(response.accountNumber).isEqualTo("000000000000")
        assertCiphertextRetained(user.id!!, bank, account)
    }

    @Test
    fun `own profile lookup does not persist decrypted fields on flush`() {
        val user = savedUser()
        val bank = user.bank
        val account = user.accountNumber
        val response = service.findDecryptedUserById(SessionUser.fromUser(user))
        assertThat(response).isNotSameAs(user)
        assertThat(response.bank).isEqualTo("테스트은행")
        assertThat(response.accountNumber).isEqualTo("000000000000")
        assertCiphertextRetained(user.id!!, bank, account)
    }
}
