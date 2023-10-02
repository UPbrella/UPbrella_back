package upbrella.be.config.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import upbrella.be.user.dto.response.SessionUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        SessionUser user = (SessionUser) session.getAttribute("user");

        if (user.getAdminStatus() == false) {
            request.getRequestDispatcher("/api/error").forward(request, response);
            return false;
        }
        

        // 세션 갱신
        request.setAttribute("user", session.getAttribute("user"));

        return true;
    }
}
