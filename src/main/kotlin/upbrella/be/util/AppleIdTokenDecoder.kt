package upbrella.be.util

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import upbrella.be.user.dto.response.AppleLoginResponse
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64

@Component
class AppleIdTokenDecoder(
    private val objectMapper: ObjectMapper,
    private val restTemplate: RestTemplate
) {

    private data class ApplePublicKey(
        val kty: String,
        val kid: String,
        val use: String,
        val alg: String,
        val n: String,
        val e: String
    )

    private data class ApplePublicKeys(
        val keys: List<ApplePublicKey>
    )

    fun decodeIdToken(idToken: String): AppleLoginResponse {
        // 1. ID Token 검증
        val claims = verifyAndParseIdToken(idToken)

        // 2. Claims에서 사용자 정보 추출
        return AppleLoginResponse(
            sub = claims["sub"] as? String,
            email = claims["email"] as? String,
            emailVerified = claims["email_verified"] as? Boolean
        )
    }

    private fun verifyAndParseIdToken(idToken: String): Claims {
        // 1. Apple 공개키 가져오기
        val publicKeys = getApplePublicKeys()

        // 2. ID Token의 header에서 kid 추출
        val header = parseHeader(idToken)
        val kid = header["kid"] as? String ?: throw IllegalArgumentException("kid not found in token header")

        // 3. kid에 맞는 공개키 찾기
        val matchingKey = publicKeys.keys.find { it.kid == kid }
            ?: throw IllegalArgumentException("No matching public key found for kid: $kid")

        // 4. 공개키 생성
        val publicKey = generatePublicKey(matchingKey)

        // 5. JWT 검증 및 파싱
        return try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(idToken)
                .body
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid ID token: ${e.message}", e)
        }
    }

    private fun getApplePublicKeys(): ApplePublicKeys {
        val response = restTemplate.getForEntity(
            "https://appleid.apple.com/auth/keys",
            ApplePublicKeys::class.java
        )

        return response.body ?: throw IllegalStateException("Failed to get Apple public keys")
    }

    private fun parseHeader(idToken: String): Map<String, Any> {
        val headerEncoded = idToken.split(".")[0]
        val headerDecoded = String(Base64.getUrlDecoder().decode(headerEncoded))
        return objectMapper.readValue(headerDecoded, Map::class.java) as Map<String, Any>
    }

    private fun generatePublicKey(key: ApplePublicKey): PublicKey {
        val nBytes = Base64.getUrlDecoder().decode(key.n)
        val eBytes = Base64.getUrlDecoder().decode(key.e)

        val n = BigInteger(1, nBytes)
        val e = BigInteger(1, eBytes)

        val publicKeySpec = RSAPublicKeySpec(n, e)
        val keyFactory = KeyFactory.getInstance("RSA")

        return keyFactory.generatePublic(publicKeySpec)
    }
}
