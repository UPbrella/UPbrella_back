package upbrella.be.util;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.HOTPGenerator;
import org.apache.commons.codec.binary.Base32;

public class HotpGenerator {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String generate(int count, String secretKey) {
        Base32 codec = new Base32();
        byte[] secret = hexStringToByteArray(secretKey);

        secret = codec.encode(secret);

        HOTPGenerator generator = new HOTPGenerator.Builder(secret)
                .withAlgorithm(HMACAlgorithm.SHA512)
                .withPasswordLength(6)
                .build();

        return generator.generate(count);
    }
}
