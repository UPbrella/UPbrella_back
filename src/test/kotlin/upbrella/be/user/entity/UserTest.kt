package upbrella.be.user.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import upbrella.be.util.AesEncryptor

class UserTest {

    @Test
    @DisplayName("사용자의 계좌 정보를 업데이트 할 수 있다.")
    fun updateBankAccountTest() {
        // given
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = "bank",
            accountNumber = "accountNumber"
        )

        val aesEncryptor = AesEncryptor("AES/CBC/PKCS5Padding", "key1234567890123")

        // when
        user.updateBankAccount("newBank", "newAccountNumber", aesEncryptor)

        // then
        assertAll(
            { assertThat(user.bank).isNotEqualTo("bank") },
            { assertThat(user.accountNumber).isNotEqualTo("accountNumber") }
        )
    }

    @Test
    @DisplayName("사용자를 삭제할 수 있다.")
    fun deleteUserTest() {
        // given
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = "bank",
            accountNumber = "accountNumber"
        )

        // when
        user.deleteUser()

        // then
        assertAll(
            { assertThat(user.socialId).isEqualTo(0L) },
            { assertThat(user.name).isEqualTo("탈퇴한 회원") },
            { assertThat(user.phoneNumber).isEqualTo("deleted") },
            { assertThat(user.adminStatus).isEqualTo(false) },
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }

    @Test
    @DisplayName("사용자를 정지시킬 수 있다.")
    fun withdrawUserTest() {
        // given
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = "bank",
            accountNumber = "accountNumber"
        )

        // when
        user.withdrawUser()

        // then
        assertAll(
            { assertThat(user.socialId).isEqualTo(0L) },
            { assertThat(user.name).isEqualTo("정지된 회원") },
            { assertThat(user.email).isEqualTo("deleted") },
            { assertThat(user.phoneNumber).isEqualTo("deleted") },
            { assertThat(user.adminStatus).isEqualTo(false) },
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }

    @Test
    @DisplayName("사용자의 정보를 복호화할 수 있다.")
    fun decryptDataTest() {
        // given
        val aesEncryptor = AesEncryptor("AES/CBC/PKCS5Padding", "key1234567890123")
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = aesEncryptor.encrypt("bank"),
            accountNumber = aesEncryptor.encrypt("accountNumber")
        )
        // when
        user.decryptData(aesEncryptor)

        // then
        assertAll(
            { assertThat(user.bank).isEqualTo("bank") },
            { assertThat(user.accountNumber).isEqualTo("accountNumber") }
        )
    }

    @Test
    @DisplayName("사용자의 계좌 정보를 삭제할 수 있다.")
    fun deleteBankAccountTest() {
        // given
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = "bank",
            accountNumber = "accountNumber"
        )

        // when
        user.deleteBankAccount()

        // then
        assertAll(
            { assertThat(user.bank).isNull() },
            { assertThat(user.accountNumber).isNull() }
        )
    }

    @Test
    @DisplayName("사용자의 어드민 권한을 업데이트할 수 있다.")
    fun updateAdminStatusTest() {
        // given
        val user = User(
            id = 1L,
            socialId = 1L,
            name = "testUser",
            phoneNumber = "123-456-7890",
            email = "email@example.com",
            adminStatus = false,
            bank = "bank",
            accountNumber = "accountNumber"
        )

        // when
        user.updateAdminStatus()

        // then
        assertThat(user.adminStatus).isTrue()
    }
}