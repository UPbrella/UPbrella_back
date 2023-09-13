package upbrella.be.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AesEncryptor {

    private String ALGORITHM;
    private String KEY_STR;
    private byte[] KEY;

    @Autowired
    public AesEncryptor(@Value("${ALGORITHM}") String algorithm,
                        @Value("${SECRET_KEY}") String keyStr) {
        this.ALGORITHM = algorithm;
        this.KEY_STR = keyStr;
        this.KEY = KEY_STR.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String data) {

        if (data == null) {
            return null;
        }

        byte[] encryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedData) {

        if (encryptedData == null) {
            return null;
        }

        byte[] decryptedBytes = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(KEY, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
