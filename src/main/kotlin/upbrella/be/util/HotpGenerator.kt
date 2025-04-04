package upbrella.be.util

import com.bastiaanjansen.otp.HMACAlgorithm
import com.bastiaanjansen.otp.HOTPGenerator
import org.apache.commons.codec.binary.Base32

class HotpGenerator {
    companion object {
        fun hexStringToByteArray(s: String): ByteArray {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4) +
                        Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }

        fun generate(count: Int, secretKey: String): String {
            val codec = Base32()
            var secret = hexStringToByteArray(secretKey)

            secret = codec.encode(secret)

            val generator = HOTPGenerator.Builder(secret)
                .withAlgorithm(HMACAlgorithm.SHA512)
                .withPasswordLength(6)
                .build()

            return generator.generate(count.toLong())
        }
    }
}
