package upbrella.be.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.nio.file.Path
import java.util.Base64

class AppleJwtGeneratorTest {

    private val generator = AppleJwtGenerator()

    private fun header(token: String): String =
        String(Base64.getUrlDecoder().decode(token.split(".")[0]))

    @Test
    @DisplayName("접두사 없는 경로와 classpath: 경로는 클래스패스에서 p8 키를 읽는다.")
    fun classpathKey() {
        for (path in listOf("keys/AuthKey_TEST.p8", "classpath:keys/AuthKey_TEST.p8")) {
            val token = generator.generateClientSecret("TEAMID1234", "KEYID12345", "com.example.app", path)

            assertThat(token.split(".")).hasSize(3)
            assertThat(header(token)).contains("\"kid\":\"KEYID12345\"")
        }
    }

    @Test
    @DisplayName("file: 경로는 이미지 밖에 마운트한 p8 키를 읽는다.")
    fun fileKey(@TempDir dir: Path) {
        val key = dir.resolve("AuthKey_TEST.p8")
        ClassPathResource("keys/AuthKey_TEST.p8").inputStream.use { Files.copy(it, key) }

        val token = generator.generateClientSecret("TEAMID1234", "KEYID12345", "com.example.app", "file:$key")

        assertThat(token.split(".")).hasSize(3)
        assertThat(header(token)).contains("\"alg\":\"ES256\"")
    }
}
