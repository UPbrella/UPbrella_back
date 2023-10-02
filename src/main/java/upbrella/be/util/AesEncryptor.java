package upbrella.be.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
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

            byte[] iv = new byte[16]; // IV length should be 16 bytes for AES
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            byte[] finalData = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, finalData, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, finalData, iv.length, encryptedBytes.length);

            encryptedBytes = finalData;
        } catch (Exception e) {
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

            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[16]; // extracting IV from encrypted data
            System.arraycopy(decodedData, 0, iv, 0, 16);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            decryptedBytes = cipher.doFinal(decodedData, 16, decodedData.length - 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
