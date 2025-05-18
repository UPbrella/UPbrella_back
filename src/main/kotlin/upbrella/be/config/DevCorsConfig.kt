package upbrella.be.config

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Profile("dev")
@Component
class DevCorsConfig : Filter {

    override fun init(filterConfig: FilterConfig) {
        // No initialization needed
    }

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val request = req as HttpServletRequest
        val response = res as HttpServletResponse

        if (request.getHeader("Origin") != null) {
            if (request.getHeader("Origin").contains("upbrella-dev.site")) {
                response.setHeader("Access-Control-Allow-Origin", "http://upbrella-dev.site")
            } else {
                response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000")
            }
        } else {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000")
        }

        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, DELETE, HEAD, OPTIONS")
        response.setHeader("Access-Control-Max-Age", "10")
        response.setHeader("Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization")

        chain.doFilter(request, response)
    }

    override fun destroy() {
        // No cleanup needed
    }
}
