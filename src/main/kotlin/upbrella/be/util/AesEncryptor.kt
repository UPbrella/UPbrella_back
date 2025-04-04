package upbrella.be.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesEncryptor @Autowired constructor(
    @Value("\${ALGORITHM}") private val algorithm: String,
    @Value("\${SECRET_KEY}") private val keyStr: String
) {
    private val key: ByteArray = keyStr.toByteArray(StandardCharsets.UTF_8)

    fun encrypt(data: String?): String? {
        if (data == null) {
            return null
        }

        var encryptedBytes: ByteArray? = null
        try {
            val cipher = Cipher.getInstance(algorithm)
            val secretKey = SecretKeySpec(key, "AES")

            val iv = ByteArray(16) // IV length should be 16 bytes for AES
            SecureRandom().nextBytes(iv)
            val ivSpec = IvParameterSpec(iv)

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            encryptedBytes = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))

            val finalData = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, finalData, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, finalData, iv.size, encryptedBytes.size)

            encryptedBytes = finalData
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun decrypt(encryptedData: String?): String? {
        if (encryptedData == null) {
            return null
        }

        var decryptedBytes: ByteArray? = null
        try {
            val cipher = Cipher.getInstance(algorithm)
            val secretKey = SecretKeySpec(key, "AES")

            val decodedData = Base64.getDecoder().decode(encryptedData)
            val iv = ByteArray(16) // extracting IV from encrypted data
            System.arraycopy(decodedData, 0, iv, 0, 16)

            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

            decryptedBytes = cipher.doFinal(decodedData, 16, decodedData.size - 16)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return decryptedBytes?.let { String(it, StandardCharsets.UTF_8) }
    }
}
