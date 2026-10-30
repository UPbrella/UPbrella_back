package upbrella.be.config.interceptor

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.repository.UserRepository

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException

@Component
class LoginInterceptor(
    private val userRepository: UserRepository
) : HandlerInterceptor {

    private fun unauthorized(response: HttpServletResponse) {
        response.status = 401
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("""{"status":"unauthorized","code":401,"message":"로그인이 필요합니다.","data":null}""")
    }

    @Throws(IOException::class, ServletException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false)
            ?: run {
                unauthorized(response)
                return false
            }

        val user = session.getAttribute("user") as? SessionUser
            ?: run {
                // For cases where Kakao authentication isn't done or session doesn't exist
                unauthorized(response)
                return false
            }

        if (!userRepository.existsById(user.id)) {
            unauthorized(response)
            return false
        }

        // Session renewal
        request.setAttribute("user", session.getAttribute("user"))
        return true
    }
}
