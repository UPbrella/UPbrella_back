package upbrella.be.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesEncryptor {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "1234567890123456".getBytes(StandardCharsets.UTF_8);

    public static String encrypt(String data) {

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

    public static String decrypt(String encryptedData) {

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
