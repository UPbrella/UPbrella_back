package upbrella.be.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.StringReader
import java.security.PrivateKey
import java.util.*

@Component
class AppleJwtGenerator {

    /**
     * Apple p8 키 파일로 JWT Client Secret 생성
     *
     * @param teamId Apple Developer Team ID (10자리)
     * @param keyId p8 키 파일의 Key ID
     * @param clientId Apple Service ID (com.example.app)
     * @param p8KeyPath 클래스패스 내 p8 파일 경로 (예: "keys/AuthKey_ABCD123456.p8")
     * @return JWT 토큰 문자열
     */
    fun generateClientSecret(
        teamId: String,
        keyId: String,
        clientId: String,
        p8KeyPath: String
    ): String {
        val now = Date()
        val expirationTime = Date(now.time + 15777000000L) // 6개월 (최대)

        val privateKey = loadPrivateKey(p8KeyPath)

        return Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(teamId)
            .setIssuedAt(now)
            .setExpiration(expirationTime)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .signWith(privateKey, SignatureAlgorithm.ES256)
            .compact()
    }

    /**
     * p8 파일에서 PrivateKey 로드
     */
    private fun loadPrivateKey(p8KeyPath: String): PrivateKey {
        val resource = ClassPathResource(p8KeyPath)
        val p8Content = resource.inputStream.bufferedReader().use { it.readText() }

        val pemParser = PEMParser(StringReader(p8Content))
        val privateKeyInfo = pemParser.readObject() as PrivateKeyInfo
        pemParser.close()

        return JcaPEMKeyConverter().getPrivateKey(privateKeyInfo)
    }
}
