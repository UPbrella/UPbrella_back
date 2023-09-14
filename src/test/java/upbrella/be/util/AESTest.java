package upbrella.be.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class AESTest {

    @Autowired
    private AesEncryptor aesEncryptor;

    @Test
    public void testEncrypt() {
        String plainText = "Hello World!";
        String encryptedText = aesEncryptor.encrypt(plainText);
        assertEquals(encryptedText, aesEncryptor.encrypt(plainText)); // 동일한 입력에 대한 출력이 동일함을 확인
    }

    @Test
    public void testDecrypt() {
        String plainText = "Hello World!";
        String encryptedText = aesEncryptor.encrypt(plainText);
        String decryptedText = aesEncryptor.decrypt(encryptedText);
        assertEquals(plainText, decryptedText); // 원래의 평문과 복호화된 텍스트가 동일함을 확인
    }

    @Test
    public void testEncryptNullInput() {
        String encryptedText = aesEncryptor.encrypt(null);
        assertNull(encryptedText); // 입력이 null인 경우 출력도 null이어야 함을 확인
    }

    @Test
    public void testDecryptNullInput() {
        String decryptedText = aesEncryptor.decrypt(null);
        assertNull(decryptedText); // 입력이 null인 경우 출력도 null이어야 함을 확인
    }
}
