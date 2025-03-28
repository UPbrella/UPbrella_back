package upbrella.be.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AesTest @Autowired constructor(
    private val aesEncryptor: AesEncryptor,
){
    @Test
    @DisplayName("복호화 테스트")
    fun testDecrypt() {
        // given
        val plainText = "Hello World!"
        val encryptedText = aesEncryptor.encrypt(plainText)

        // when
        val decryptedText = aesEncryptor.decrypt(encryptedText)

        // then
        assertThat(decryptedText).isEqualTo("Hello World!")
    }

    @Test
    @DisplayName("Null을 암호화하는 경우 출력도 Null")
    fun testEncryptNullInput() {
        // given
        val nullString : String? = null

        // when
        val encryptedText = aesEncryptor.encrypt(nullString)

        // then
        assertThat(encryptedText).isNull()
    }

    @Test
    @DisplayName("Null을 복호화 하는 경우 경우 출력도 Null")
    fun testDecryptNullInput() {
        // given
        val nullString : String? = null

        // when
        val decryptedText = aesEncryptor.decrypt(nullString)

        // then
        assertThat(decryptedText).isNull()
    }
}
