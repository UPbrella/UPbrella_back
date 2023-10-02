package upbrella.be.config.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import upbrella.be.user.dto.response.SessionUser;
import upbrella.be.user.repository.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            request.getRequestDispatcher("/api/error").forward(request, response);
            return false;
        }

        SessionUser user = (SessionUser) session.getAttribute("user");

        // 카카오 인증이 안되어 있는 경우, 세션이 없는 경우
        if (user == null) {
            request.getRequestDispatcher("/api/error").forward(request, response);
            return false;
        }

        if (!userRepository.existsById(user.getId())) {
            request.getRequestDispatcher("/api/error").forward(request, response);
            return false;
        }
        // 세션 갱신
        request.setAttribute("user", session.getAttribute("user"));
        return true;
    }
}

