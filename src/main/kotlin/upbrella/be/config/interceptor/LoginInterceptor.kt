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

    @Throws(IOException::class, ServletException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false)
            ?: run {
                request.getRequestDispatcher("/api/error").forward(request, response)
                return false
            }

        val user = session.getAttribute("user") as? SessionUser
            ?: run {
                // For cases where Kakao authentication isn't done or session doesn't exist
                request.getRequestDispatcher("/api/error").forward(request, response)
                return false
            }

        if (!userRepository.existsById(user.id)) {
            request.getRequestDispatcher("/api/error").forward(request, response)
            return false
        }

        // Session renewal
        request.setAttribute("user", session.getAttribute("user"))
        return true
    }
}
