package upbrella.be.config.interceptor

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import upbrella.be.user.dto.response.SessionUser

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AdminInterceptor(private val users: upbrella.be.user.repository.UserRepository) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session = request.getSession(false) ?: run {
            response.status = 401
            return false
        }

        val user = session.getAttribute("user") as? SessionUser ?: run {
            response.status = 401
            return false
        }

        if (!users.findById(user.id).map { it.adminStatus }.orElse(false)) {
            response.status = 403
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("""{"status":"forbidden","code":403,"message":"운영진 권한이 필요합니다.","data":null}""")
            return false
        }

        // Session renewal
        request.setAttribute("user", session.getAttribute("user"))

        return true
    }
}
