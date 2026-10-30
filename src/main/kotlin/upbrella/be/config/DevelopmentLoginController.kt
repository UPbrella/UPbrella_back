package upbrella.be.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import upbrella.be.user.entity.User
import upbrella.be.user.repository.UserRepository
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.util.CustomResponse
import java.security.MessageDigest
import javax.servlet.http.HttpServletRequest

@RestController
@Profile("home-dev")
class DevelopmentLoginController(
    private val users: UserRepository,
    @Value("\${upbrella.dev-login.enabled:false}") private val enabled: Boolean,
    @Value("\${upbrella.dev-login.key:}") private val key: String
) {
    data class LoginRequest(val key: String, val role: String = "user")

    @PostMapping("/dev/login")
    fun login(@RequestBody input: LoginRequest, request: HttpServletRequest): CustomResponse<Map<String, Any>> {
        if (!enabled || key.length < 24 || !MessageDigest.isEqual(key.toByteArray(), input.key.toByteArray()))
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "개발 접속 키가 올바르지 않습니다.")
        if (input.role !in setOf("user", "admin")) throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        val socialId = if (input.role == "admin") -260201L else -260202L
        val user = users.findBySocialId(socialId).orElseGet {
            users.save(User(socialId = socialId, name = if (input.role == "admin") "개발 관리자" else "개발 테스트 사용자",
                email = "", provider = "DEV", adminStatus = input.role == "admin"))
        }
        request.getSession(false)?.invalidate()
        request.session.setAttribute("user", SessionUser.fromUser(user))
        return CustomResponse("success", 200, "개발 로그인 완료", mapOf("id" to user.id!!, "adminStatus" to user.adminStatus))
    }
}
