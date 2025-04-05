package upbrella.be.config.interceptor

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import upbrella.be.user.dto.response.SessionUser

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AdminInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false) ?: run {
            request.getRequestDispatcher("/api/error").forward(request, response)
            return false
        }

        val user = session.getAttribute("user") as SessionUser

        if (user.adminStatus == false) {
            request.getRequestDispatcher("/api/error").forward(request, response)
            return false
        }

        // Session renewal
        request.setAttribute("user", session.getAttribute("user"))

        return true
    }
}
